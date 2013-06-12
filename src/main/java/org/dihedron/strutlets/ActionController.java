/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.strutlets;


import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.actions.Result;
import org.dihedron.strutlets.actions.factory.ActionFactory;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.dihedron.strutlets.interceptors.registry.InterceptorsRegistry;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.strutlets.renderers.impl.CachingRendererRegistry;
import org.dihedron.strutlets.renderers.impl.JspRenderer;
import org.dihedron.strutlets.renderers.registry.RendererRegistry;
import org.dihedron.strutlets.renderers.registry.RendererRegistryLoader;
import org.dihedron.strutlets.targets.TargetData;
import org.dihedron.strutlets.targets.TargetId;
import org.dihedron.strutlets.targets.registry.TargetFactory;
import org.dihedron.strutlets.targets.registry.TargetRegistry;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Strutlets framework controller portlet.
 * 
 * The role of the Strutlets controller is that of:<ol>
 * <li>analysing the incoming request, no matter whether an action, render, event 
 * or resource request, and checks if it contains a valid target identifier</li>
 * <li>dispatching control to the appropriate target handler</li>
 * <li>gathering the target's results and identifying the appropriate renderer</li>
 * <li>dispatching control to the renderer.</li></ol>
 * According to JSR-286, there can only be one single instance of each declared
 * action controller portlet per virtual machine, so no issues can arise regarding
 * the cost of initialising the registries: the operation is performed only once
 * per portlet container:<br>
 * <b>PLT.5.1 Number of Portlet Instances</b>
 * The portlet definition sections in the deployment descriptor of a portlet 
 * application control how the portlet container creates portlet instances.
 * For a portlet, not hosted in a distributed environment (the default), the portlet
 * container <em>must instantiate and use only one portlet object per portlet 
 * definition</em>.
 * In the case where a portlet is deployed as part of a portlet application marked as
 * distributable, in the web.xml deployment descriptor, a portlet container <em>may 
 * instantiate only one portlet object per portlet definition -in the deployment 
 * descriptor- per virtual machine (VM)</em>.
 */
public class ActionController extends GenericPortlet {
	
	/**
	 * The registry of interceptors' stacks.
	 */
	private InterceptorsRegistry interceptors;
	
	/**
	 * The registry of supported targets.
	 */
	private TargetRegistry registry;
	
	/**
	 * The registry of supported renderers.
	 */
	private RendererRegistry renderers;

    /**
     * Initialises the controller portlet. 
     * 
     * The process follows these steps:<ol>
     * <li><b>loading the targets registry</b>: the target registry is responsible 
     * for providing the right action and method combination (the so-called 
     * "target") for the current request; for more details on targets see 
     * {@link org.dihedron.strutlets.targets.TargetId TargetId} and 
     * {@link org.dihedron.strutlets.targets.TargetData TargetData}</li>
     * <li><b>loading the interceptors stacks</b>: the framework already provides 
     * a standard set of interceptors, but the user can provide her own by 
     * setting a value for the <code>???</code> parameter and placing a file with
     * the relevant information under the same path. User stacks override default 
     * stacks if names collide.</li>
     * <li><b>loading the renderers registry</b>: this is the registry of available 
     * renderers.</li></ol>
     * </ol>
     * 
     * @see javax.portlet.GenericPortlet#init()
     */
    public void init() throws PortletException {
    	super.init();
       
        try {
        	logger.info(              "   +--------------------------------+   ");
        	logger.info(String.format("   |      STRUTLETS ver. %1$-8s   |   ", Strutlets.getVersion()));
        	logger.info(              "   +--------------------------------+   ");
        	
        	logger.info("action controller '{}' starting up...", getPortletName());
        	
        	// dump initialisation parameters for debugging purposes
        	for(InitParameter parameter : InitParameter.values()) {
        		logger.info(" + parameter: {}", parameter.toString(this));
        	}

        	initialiseTargetsRegistry();
			
        	initialiseInterceptorsRegistry();
        	
        	initialiseRenderersRegistry();
			
			logger.info("portlet '{}''s action controller open for business", getPortletName());
			
		} catch (StrutletsException e) {
			logger.error("error initialising controller portlet");
			throw e;
		}
    }

    /**
     * Intercepts action requests and dispatches them to the appropriate handler.
     * This method receives incoming action requests and looks up the appropriate
     * handler in the target map, then propagates the request to the action through
     * the interceptors stack. Once the processing is complete, it gathers the 
     * result string and looks up the appropriate renderer (e.g. a JSP).
     * 
     *  @param request
     *    the incoming <code>ActionRequest</code> object.
     *  @param response
     *    the <code>ActionResponse</code> object. 
     * @see 
     *   javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws IOException, PortletException {
    	logger.trace("processing action...");
    	
    	// check if the target is contained in the request
    	TargetId targetId = TargetId.makeFromRequest(request);
    	    	
    	String result = invokeBusinessLogic(targetId, request, response);
    	
    	logger.trace("... action processing done with result '{}'", result);
    }
    
    /**
     * Extracts the name of the event from the JSR-286 events, re-maps it onto a
     * target (if available) and then dispatches control to the appropriate action
     * if one was found.
     * 
     *  @param request
     *    the incoming <code>EventRequest</code> object.
     *  @param response
     *    the <code>EventResponse</code> object. 
     * @see 
     *   javax.portlet.GenericPortlet#processEvent(javax.portlet.EventRequest, javax.portlet.EventResponse)
     */
    @Override
    public void processEvent(EventRequest request, EventResponse response) throws PortletException, IOException {
    	logger.trace("processing event...");
    	// get the name of the event and re-map it onto the target through the registry
    	QName qname = request.getEvent().getQName();
    	TargetId targetId = registry.getEventTarget(qname);
    	
    	String result = invokeBusinessLogic(targetId, request, response);
    	
    	logger.trace("... event processing done with result '{}'", result);
    }        
    
    /**
     * The method that perform output rendering <em>in JSP format</em>; it might 
     * include some idempotent business logic prior to rendering the output.
     * 
     * This method performs some action in order to understand whether the render
     * phase is immediately following the processing of an action or an event (in 
     * which case we should not expect any further TargetData to be invoked), or it
     * is in response to a render request.
     * In the former case, the method will simply identify the JSP page to invoke
     * by accessing the appropriate {@code Result} in the targets registry and 
     * then it will yield control to the JSP for rendering.
     * In the latter case, the method will test if the provided JSP is actually 
     * a target specification (in the usual format "MyAction!myMethod"), and if
     * so it will forward control to the action and only subsequently will it
     * yield to the JSP, again identified according to the target's results 
     * information.
     * This method perform all that is necessary to provide proper output in the 
     * render phase. It proceeds as follows:<ol>
     * <li>checks if the render phase immediately follows an action/event phase 
     * for this portlet, in which case the {@code STRUTLETS_TARGET} and the 
     * {@code STRUTLETS_RESULT} render parameters will be set and can be used to
     * retrieve the appropriate {@code Result} object;</li>
     * <li>the rendering is due to a render request (through a {@code RenderURL}),
     * in which case there are two subcases:<ol>
     * <li>the request contains a target, and some business logic must be executed
     * before getting to the URL to render;</li>
     * <li>the request contains a plain URL, so no further processing is needed;</li>looks for a target in the request;</li>
     * </ol>
     * <li>the request comes because the homepage is being displayed to the user,
     * which has two cases:<ol>
     * <li>the initial homepage URL is a target;</li>
     * <li>the initial homepage is a plain URL.</li>
     * </ol>
     * The method checks for targets and executes them, unless a plain URL has been 
     * given; then it moves on to rendering <em>in JSP mode</em> the resulting URL.
     * Render URLs are expected to only return JSPs.
     *   
     * @param request
     *   the render request.
     * @param response
     *   the render response.
     * @see javax.portlet.GenericPortlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {

    	TargetId targetId = null;
    	String result = null;
    	
    	Renderer renderer = null;
    	
    	logger.trace("rendering output...");
    	
    	do {
	    	// first attempt, check if this is a render request following an action/event request
    		String target = request.getParameter(Strutlets.STRUTLETS_TARGET);
    		result = request.getParameter(Strutlets.STRUTLETS_RESULT);
    		if(TargetId.isValidTarget(target) && Strings.isValid(result)) {
    			// yes, this is right after a processAction or processEvent
    			targetId = new TargetId(target);
    			logger.trace("rendering after action/event target '{}' invoked with result '{}'", targetId, result);
    			break;
    		}
    		
    		logger.trace("no prior action/event phase to get renderer from");
    		
	    	// second attempt, check if there is a target to invoke before rendering
	    	targetId = TargetId.makeFromRequest(request);
	    	if(targetId != null) {
	    		logger.trace("invoking target '{}' as per render request", targetId);
		    	
	    		// this is a valid target specification, dispatch to the appropriate 
	    		// action and method, then retrieve the result's URL	    		
	    		result = invokePresentationLogic(targetId, request, response);
	    		
	    		// task invoked, now exit
	    		break;
	    	}
	    	
	    	// last attempt, if there is no valid URL (no target!) in the request, 
	    	// then this is the initial page and we might have a target there too 
	    	if(!Strings.isValid(request.getParameter(Strutlets.LIFERAY_TARGET))) {
	    		target = getDefaultUrl(request.getPortletMode());
	    		if(TargetId.isValidTarget(target)) { // (*)
	    			targetId = new TargetId(target);
		    		logger.trace("invoking target '{}' as per default URL for mode '{}'", targetId, request.getPortletMode());
			    	
		    		// this is a valid target specification, dispatch to the appropriate 
		    		// action and method, then retrieve the result's URL	    		
		    		result = invokePresentationLogic(targetId, request, response);
		    		
		    		// task invoked, now exit
		    		break;
	    		} else {
	    			target = null;
	    		}
	    	}
    		
    		logger.trace("no business logic to invoke in render phase");
    		
    	} while(false);   

    	String url = null;
    	// now, if target and result are valid, get the renderer
    	if(targetId != null && Strings.isValid(result)) {
    		// get the URL corresponding to the given target and result
    		url = getUrl(targetId, result);
    	} else {
			// no, this is a plain render request, let's check "jspPage" 
			// first, to comply with Liferay's worst practices
    		url = request.getParameter(Strutlets.LIFERAY_TARGET);
    		if(Strings.isValid(url)) {
    			logger.trace("redirecting to jspPage '{}' as requested by client", url);
    		} else {		    			
    			// we have to resort to the default URL for the given mode,
    			// the ones specified in the portlet parameters; at this point
    			// we are sure it's not a target (otherwise it would have been 
    			// detected as such above, at (*)		    			
	    		PortletMode mode = request.getPortletMode();
	    		logger.trace("redirecting to default page for current mode '{}'", mode.toString());
	    		url = getDefaultUrl(mode);
    		}
    	}

	    // we're almost done: proceed with the JSP rendering
    	if(Strings.isValid(url)) {
    		logger.info("rendering through URL: '{}'", url);
    		renderer = renderers.getRenderer(JspRenderer.ID);
    		renderer.setData(url);
    		renderer.render(request, response);    		
    	} else {
    		logger.error("invalid render URL");
    		throw new StrutletsException("No valid render URL available");
    	}    	
    	logger.trace("... output rendering done");
    }
    
    /**
     * Serves a resource.
     * 
     * @see javax.portlet.GenericPortlet#serveResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
     */
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
    	
    	String target = request.getResourceID();
    	logger.trace("serving resource '{}'...", target);
    	if(TargetId.isValidTarget(target)) {
    		logger.trace("... executing business logic to gather the resource");
    		
    		TargetId targetId = new TargetId(target);
    		
    		String res = invokeResourceLogic(targetId, request, response);
    		
    		logger.trace("target '{}' returned '{}'", targetId, res);
    		
			TargetData data = registry.getTarget(target);
			Result result = data.getResult(res);
			
			logger.debug("rendering via '{}', result data '{}'...", result.getRenderer(), result.getData());
    		
    		Renderer renderer = renderers.getRenderer(result.getRenderer());
    		renderer.setData(result.getData());
        	renderer.render(request, response);
        	
        	logger.trace("... output rendering done");
    		
    	} else {
    		logger.trace("... leaving the resource request to the portlet container");
    		super.serveResource(request, response);
    	}
    }    

    
    /**
     * Processes an action request in the action and event phases.
     * 
     * @param targetId
     *   the requested target (in the form "MyAction!myMethod").
     * @param request
     *   the current request object.
     * @param response
     *   the current action or event response object.
     * @throws PortletException
     */
    protected String invokeBusinessLogic(TargetId targetId, PortletRequest request, StateAwareResponse response) throws PortletException {
    	String res = null;
    	
    	try {
			if(targetId != null) {
				// invoke method
				logger.debug("invoking target '{}'...", targetId);    			
	    		res = invokeTarget(targetId, request, response);
	    		
	    		// get routing configuration for given result string
	    		Result result = registry.getTarget(targetId).getResult(res);
	    		PortletMode mode = result.getPortletMode();
	    		WindowState state = result.getWindowState();
	    		
	    		logger.debug("... target '{}' returned result: '{}', mode: '{}', state: '{}'", targetId, result.getId(), mode, state);
	
	    		// change portlet mode	    		
	    		if(Strings.isValid(mode.toString()) && request.isPortletModeAllowed(mode)) {
	    			response.setPortletMode(mode);
	    		}
	    		// change window state	    		
	    		if(Strings.isValid(state.toString()) && request.isWindowStateAllowed(state)) {
	    			response.setWindowState(state);
	    		}
	    		// set parameters for the following render phase
		    	response.setRenderParameter(Strutlets.STRUTLETS_TARGET, targetId.toString());
		    	response.setRenderParameter(Strutlets.STRUTLETS_RESULT, result.getId());	    		
			} else {			
				// action not specified, check the current mode and service the
				// default page, as specified in the initialisation parameters
				logger.trace("target not specified, blanking target and result in render parameters and serving default page");
		    	response.setRenderParameter(Strutlets.STRUTLETS_TARGET, (String)null);
		    	response.setRenderParameter(Strutlets.STRUTLETS_RESULT, (String)null);    			
			}
		} catch(PortletException e) {
			logger.error("portlet exception servicing action request: {}", e.getMessage());
			throw e;
		}    	
		return res;
    }
    
    protected String invokePresentationLogic(TargetId targetId, RenderRequest request, RenderResponse response) throws IOException, PortletException {
    	String result = null;
    	try {
    		if(targetId != null) {
    			
    			// check that the method is for presentation
    			TargetData targetData = registry.getTarget(targetId);
    			if(!targetData.isIdempotent()) {
    				throw new PortletException("Trying to invoke non-idempotent method in render request");
    			}
    			
    			// invoke method
    			logger.debug("invoking target '{}'...", targetId);    			
	    		result = invokeTarget(targetId, request, response);	
	    		logger.debug("... target '{}' result is '{}'", targetId, result);
    		}
    		return result;
    	} catch(PortletException e) {
    		logger.error("portlet exception servicing render request: {}", e.getMessage());
    		throw e;
		}
    }    
    
    protected String invokeResourceLogic(TargetId targetId, ResourceRequest request, ResourceResponse response) throws IOException, PortletException {
    	String result = null;
    	try {
    		if(targetId != null) {    			
    			logger.debug("invoking target '{}'...", targetId);    			
	    		result = invokeTarget(targetId, request, response);	
	    		logger.debug("... target '{}' result is '{}'", targetId, result);	    		
    		}
    	} catch(PortletException e) {
    		logger.error("portlet exception servicing render request: {}", e.getMessage());
    		throw e;
		}
    	return result;
    }        
    
    
    protected String invokeTarget(TargetId targetId, PortletRequest request, PortletResponse response) throws StrutletsException {
    	Action action = null;
    	String result = null;
    	try {
    		logger.info("invoking target '{}'", targetId);
    		
    		// check if there's configuration available for the given action
			TargetData targetData = registry.getTarget(targetId);
			
			logger.trace("target configuration:\n{}", targetData.toString());
    		
			// instantiate the action
			action = ActionFactory.makeAction(targetData);
			if(action != null) {
				logger.info("action '{}' ready", targetData.getAction().getSimpleName());
			} else {    			 	
				logger.error("no action found for target '{}'", targetId);
				throw new StrutletsException("No action could be found for target '" + targetId + "'");
			}
			
			// get the stack for the given action
			InterceptorStack stack = interceptors.getStackOrDefault(targetData.getInterceptorStackId());

			// create the invocation object
	    	ActionInvocation invocation = new ActionInvocation(action, targetData.getMethod(), stack, request, response);
	    	
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
	    	ActionContextImpl.bindContext(this, request, response, invocation);
	    	
	    	// fire the action stack invocation
	    	result = invocation.invoke();
	    	
    	} finally {
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
    		ActionContextImpl.unbindContext();
    	}
    	return result;
    }
    
    /**
     * Retrieves the render URL for the given target and its result.
     * 
     * @param targetId
     *   the target.
     * @param result
     *   the target's result.
     * @return
     *   the URL that will provide the view for the given actiontarget's result.
     * @throws StrutletsException 
     */
    private String getUrl(TargetId targetId, String result) throws StrutletsException{
    	String url = null;
    	if(targetId != null && Strings.isValid(result)) {	    	
	    	logger.trace("looking for renderer for target '{}' with result '{}'", targetId, result);	    	
	    	TargetData targetData = registry.getTarget(targetId);
	    	Result renderer = targetData.getResult(result);
	    	url = renderer.getData();
	    	logger.debug("renderer URL for target '{}' with result '{}' is '{}' (mode '{}', state '{}')", 
	    			targetId, result, url, renderer.getPortletMode(), renderer.getWindowState());
    	}
    	return url;
    }
    
    /**
     * Retrieves the default URL for the given portlet mode.
     * 
     * This method checks for the URL (which could also contain the indication 
     * of a target (action + method) in the <code>portlet.xml</code> initialisation
     * parameters. For details about the names of the parameters check 
     * {@link org.dihedron.strutlets.ActionController.InitParameter}.
     *  
     * @param mode
     *   the current portlet mode.
     * @return
     *   the URL (or the target) for the given portlet mode.
     */
    private String getDefaultUrl(PortletMode mode) {
    	String url = null;    	
		if(mode.equals(PortletMode.VIEW)) {
			logger.trace("getting default URL for mode 'view'");
			url = InitParameter.RENDER_VIEW_HOMEPAGE.getValueForPortlet(this);
		} else if(mode.equals(PortletMode.EDIT)) {
			logger.trace("getting default URL for mode 'edit'");
			url = InitParameter.RENDER_EDIT_HOMEPAGE.getValueForPortlet(this);
		} else if(mode.equals(PortletMode.HELP)) {
			logger.trace("getting default URL for mode 'help'");
			url = InitParameter.RENDER_HELP_HOMEPAGE.getValueForPortlet(this);
		} else {
			logger.trace("getting default URL for custom render mode: '{}'", mode);
			String parameter = RENDER_XXXX_HOMEPAGE.replaceFirst("xxxx", mode.toString());
			url = getInitParameter(parameter);
		}
    	return url;
    }
    
    /**
     * Initialises the action registry with information taken from:<ol>
     * <li> the custom actions-config.xml file (if a custom name is provided 
     * among the initialisation parameters)</li>
     * <li> the default actions-config.xml file, if found on the classpath</li>
     * <li> the actions package, if one is provided among the initialisation 
     * parameters.</li>
     * </ol>
     * 
     * @throws StrutletsException 
     */
    private void initialiseTargetsRegistry() throws StrutletsException {
        // get the actions configuration repository
    	registry = new TargetRegistry();
    	
		// set the root directory for HTML files and JSPs, for auto-configured annotated actions
		registry.setHtmlPathInfo(InitParameter.RENDER_ROOT_DIRECTORY.getValueForPortlet(this), InitParameter.RENDER_PATH_PATTERN.getValueForPortlet(this));
		
		// pre-scan existing classes and methods in the default actions package
		TargetFactory loader = new TargetFactory();
		loader.makeFromJavaPackage(registry, InitParameter.ACTIONS_JAVA_PACKAGE.getValueForPortlet(this));

		logger.trace("actions configuration:\n{}", registry.toString());    	
    }
    
    /**
     * Initialises the interceptors stack registry (factory) by loading the default 
     * stacks first and then any custom stacks provided in the initialisation 
     * parameters.
     * 
     * @throws StrutletsException
     */
    private void initialiseInterceptorsRegistry() throws StrutletsException {

		interceptors = new InterceptorsRegistry();
		
		// load the default interceptors stacks ("default" and others)
		logger.info("loading default interceptors stacks: '{}'", InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		interceptors.loadFromClassPath(InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		logger.trace("pre-configured interceptors stacks:\n{}", interceptors.toString());
		
		// load the custom interceptors configuration
		String file = InitParameter.INTERCEPTORS_CONFIGURATION_FILE.getValueForPortlet(this);
		if(Strings.isValid(file)) {
			logger.info("loading interceptors configuration from custom location: '{}'", file);
			interceptors.loadFromClassPath(file);
			logger.trace("interceptors stacks:\n{}", interceptors.toString());
		}    	
    }
    
    /**
     * Initialises the registry of view renderers.
     * @throws StrutletsException
     */
    private void initialiseRenderersRegistry() throws StrutletsException {
    	RendererRegistryLoader loader = new RendererRegistryLoader();
    	renderers = new CachingRendererRegistry(this);
    	//renderers = new RenewingRendererRegistry(this);
    	loader.loadFromJavaPackage(renderers, RendererRegistry.DEFAULT_RENDERER_PACKAGE);
    	loader.loadFromJavaPackage(renderers, InitParameter.RENDERERS_JAVA_PACKAGE.getValueForPortlet(this));
    }

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in custom XXXX (whatever) mode. This page is the starting point of the custom mode 
	 * HTML navigation tree.
	 */	
	private static final String RENDER_XXXX_HOMEPAGE = "render.xxxx.homepage";    

	/**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);
}
