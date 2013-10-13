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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

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

import org.dihedron.commons.properties.Properties;
import org.dihedron.commons.properties.PropertiesException;
import org.dihedron.commons.url.URLFactory;
import org.dihedron.commons.utils.Strings;
import org.dihedron.strutlets.actions.Result;
import org.dihedron.strutlets.actions.factory.ActionFactory;
import org.dihedron.strutlets.containers.portlet.PortalServer;
import org.dihedron.strutlets.containers.portlet.PortalServerPluginFactory;
import org.dihedron.strutlets.containers.web.ApplicationServer;
import org.dihedron.strutlets.containers.web.ApplicationServerPluginFactory;
import org.dihedron.strutlets.exceptions.DeploymentException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.dihedron.strutlets.interceptors.registry.InterceptorsRegistry;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.PluginManager;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.strutlets.renderers.impl.CachingRendererRegistry;
import org.dihedron.strutlets.renderers.impl.JspRenderer;
import org.dihedron.strutlets.renderers.registry.RendererRegistry;
import org.dihedron.strutlets.renderers.registry.RendererRegistryLoader;
import org.dihedron.strutlets.targets.Target;
import org.dihedron.strutlets.targets.TargetId;
import org.dihedron.strutlets.targets.registry.TargetFactory;
import org.dihedron.strutlets.targets.registry.TargetRegistry;
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
	 * The action's configuration.
	 */
	private Properties configuration = null;
	
	/**
	 * The current application server.
	 */
	private ApplicationServer server;
	
	/**
	 * The current portlet container.
	 */
	private PortalServer portal;
	
	/**
	 * The default package for stock portal- and application-server plugins.
	 */
	public static final String DEFAULT_CONTAINERS_CLASSPATH = "org.dihedron.strutlets.containers";	
	
    /**
     * Initialises the controller portlet. 
     * 
     * The process follows these steps:<ol>
     * <li><b>loading the targets registry</b>: the target registry is responsible 
     * for providing the right action and method combination (the so-called 
     * "target") for the current request; for more details on targets see 
     * {@link org.dihedron.strutlets.targets.TargetId TargetId} and 
     * {@link org.dihedron.strutlets.targets.Target Target}</li>
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
	@Override
    public void init() throws PortletException {
    	super.init();
       
        try {
        	logger.info(              "   +------------------------------------------------------------+   ");
        	logger.info(              "   |                                                            |   ");
        	logger.info(String.format("   |      %1$-48s      |   ", Strings.centre(this.getPortletName().toUpperCase(), 48)));
        	logger.info(              "   |                                                            |   ");
        	logger.info(String.format("   |                                  strutlets ver. %1$-10s |   ", Strutlets.getVersion()));
        	logger.info(              "   +------------------------------------------------------------+   ");
        	
        	logger.info("action controller '{}' starting up...", getPortletName());
        	        	
        	performSanityCheck();
        	
        	initialisePortletConfiguration();
        	
        	initialiseRuntimeEnvironment();

        	initialiseTargetsRegistry();
			
        	initialiseInterceptorsRegistry();
        	
        	initialiseRenderersRegistry();
			
			logger.info("action controller for portlet '{}' open for business", getPortletName());
			
		} catch (StrutletsException e) {
			logger.error("error initialising controller portlet");
			throw e;
		}
    }
    
    @Override
    public void destroy() {
    	logger.info("action controller {} shutting down...", this.getPortletName());
    	if(portal != null) {
    		logger.trace("... cleaning up portlet container");
    		portal.cleanup();
    	}
    	if(server != null) {
    		logger.trace("... cleaning up application server");
    		server.cleanup();
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
    	try {
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
    		logger.trace("binding context to thread-local storage");
	    	ActionContextImpl.bindContext(this, request, response, configuration, server, portal);
	    	
	    	// request attributes are removed upon a brand new action request 
	    	ActionContext.clearRequestAttributes();

	    	logger.trace("processing action...");
	    		    	
	    	// check if the target is contained in the request
	    	TargetId targetId = TargetId.makeFromRequest(request);
	    	    	
	    	String result = invokeBusinessLogic(targetId, request, response);
	    	
	    	logger.trace("... action processing done with result '{}'", result);
		} finally {
			
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContextImpl.unbindContext();    			
		}
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
    	try {
    		
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
    		logger.trace("binding context to thread-local storage");
	    	ActionContextImpl.bindContext(this, request, response, configuration, server, portal);
    		
	    	// request attributes are removed upon a brand new event request
	    	ActionContext.clearRequestAttributes();
    	
	    	logger.trace("processing event...");
	    	
	    	// get the name of the event and re-map it onto the target through the registry
	    	QName qname = request.getEvent().getQName();
	    	TargetId targetId = registry.getEventTarget(qname);
	    	
	    	String result = invokeBusinessLogic(targetId, request, response);
	    	
	    	logger.trace("... event processing done with result '{}'", result);
    	} finally {

    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContextImpl.unbindContext();
    	}
    }        
    
    /**
     * The method that perform output rendering <em>in JSP format</em>; it might 
     * include some idempotent business logic prior to rendering the output.
     * 
     * This method performs some action in order to understand whether the render
     * phase is immediately following the processing of an action or an event (in 
     * which case we should not expect any further Target to be invoked), or it
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

    	try {
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
    		logger.trace("binding context to thread-local storage");
	    	ActionContextImpl.bindContext(this, request, response, configuration, server, portal);    		
    		
	    	TargetId targetId = null;
	    	Renderer renderer = null;
	    	
    		String target = request.getParameter(Strutlets.STRUTLETS_TARGET);
    		String result = request.getParameter(Strutlets.STRUTLETS_RESULT);
	    	
	    	logger.trace("rendering output (target: '{}', result: '{}')...", target, result);
	    	
	    	boolean first = true;
	    	while(true) {
	    		if(TargetId.isValidTarget(target)) {
	    			targetId = new TargetId(target);
	    			if(Strings.isValid(result)) {
	    				// there has been a target invocation, either in action/event or render phase,
	    				// that has resulted in a valid target and result, now we can get information
	    				// about what the user wants us to do when the given target returns the given
	    				// result; this can be retrieved from the target registry
	    				logger.trace("render phase after an action/event/render invocation: '{}' (first: {})", target, first);	    				
	    				Target targetData = registry.getTarget(targetId);
	    				Result r = targetData.getResult(result);
	    				if(r == null) {
	    					logger.error("misconfiguration in registry: target '{}' and result '{}' have no valid processing information", target, result);
	    					throw new StrutletsException("No valid information found in registry for target '" + target + "', result '" + result + "', please check your actions");
	    				}
	    				String subtarget = r.getData();
	    				if(TargetId.isValidTarget(subtarget)) {
	    					targetId = new TargetId(subtarget);
	    					logger.debug("target '{}' on result '{}' wants its output rendered by target '{}', forwarding...", target, result, subtarget);
	    					result = invokePresentationLogic(targetId, request, response);
	    					target = targetId.toString();
	    					continue;
		    			} else {
		    				logger.trace("moving over to rendering '{}'...", subtarget);
		    				break;
		    			}
	    			} else {
	    				logger.trace("render phase with no prior action/event/render invocation: '{}' (first: {})", target, first);
    					result = invokePresentationLogic(targetId, request, response);
	    				continue;
	    			}
	    		} else {
	    			if(first) {
	    				first = false;
	    				// no, this is a plain render request, let's check "jspPage" 
	    				// first, to comply with Liferay's worst practices
	    	    		target = request.getParameter(Strutlets.LIFERAY_TARGET);
	    	    		if(Strings.isValid(target)) {
	    	    			logger.trace("redirecting to jspPage '{}' as requested by client", target);	    	    			
	    	    		} else {		    			
	    	    			// we have to resort to the default URL for the given mode,
	    	    			// the ones specified in the portlet parameters; at this point
	    	    			// we are sure it's not a target (otherwise it would have been 
	    	    			// detected as such above, at (*)		    			
	    		    		PortletMode mode = request.getPortletMode();
	    		    		logger.trace("redirecting to default page for current mode '{}'", mode.toString());
	    		    		target = getDefaultUrl(mode);	    		    		
	    	    		}
	    	    		continue;
	    			} else {
	    				logger.trace("no valid target in request, rendering does not follow an action ('{}')", target);
	    				break;
	    			}
	    		}
	    	}    
	
	    	// when we get here, either we have a target that is a URL (such as a 
	    	// JSP) or we have no target at all and must resort to the default page
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
    	} finally {
    		
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContextImpl.unbindContext();		    		
    	}
    }
    
    /**
     * Serves a resource.
     * 
     * @see javax.portlet.GenericPortlet#serveResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
     */
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
    	
    	try {
    		
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
    		logger.trace("binding context to thread-local storage");
	    	ActionContextImpl.bindContext(this, request, response, configuration, server, portal);
	    	
	    	String target = request.getResourceID();
	    	logger.trace("serving resource '{}'...", target);
	    	if(TargetId.isValidTarget(target)) {
	    		logger.trace("... executing business logic to gather the resource");
	    		
	    		TargetId targetId = new TargetId(target);
	    		
	    		String res = invokeResourceLogic(targetId, request, response);
	    		
	    		logger.trace("target '{}' returned '{}'", targetId, res);
	    		
				Target data = registry.getTarget(target);
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
    	} finally {
    		
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContextImpl.unbindContext();
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
    			Target targetData = registry.getTarget(targetId);
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

		logger.info("invoking target '{}'", targetId);
		
		// check if there's configuration available for the given action
		Target target = registry.getTarget(targetId);
		
		logger.trace("target configuration:\n{}", target.toString());
		
		// instantiate the action
		Object action = ActionFactory.makeAction(target);
		if(action != null) {
			logger.info("action instance '{}' ready", target.getActionClass().getSimpleName());
		} else {    			 	
			logger.error("could not create an action instance for target '{}'", targetId);
			throw new StrutletsException("No action could be found for target '" + targetId + "'");
		}
		
		// get the stack for the given action
		InterceptorStack stack = interceptors.getStackOrDefault(target.getInterceptorStackId());
    	    	
    	// create and fire the action stack invocation
    	return new ActionInvocation(action, target, stack, request, response).invoke();
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
	    	Target targetData = registry.getTarget(targetId);
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
			url = InitParameter.VIEW_MODE_HOME.getValueForPortlet(this);
		} else if(mode.equals(PortletMode.EDIT)) {
			logger.trace("getting default URL for mode 'edit'");
			url = InitParameter.EDIT_MODE_HOME.getValueForPortlet(this);
		} else if(mode.equals(PortletMode.HELP)) {
			logger.trace("getting default URL for mode 'help'");
			url = InitParameter.HELP_MODE_HOME.getValueForPortlet(this);
		} else {
			logger.trace("getting default URL for custom render mode: '{}'", mode);
			String parameter = RENDER_XXXX_HOMEPAGE.replaceFirst("xxxx", mode.toString());
			url = getInitParameter(parameter);
		}
    	return url;
    }
    
    // TODO: this method will be removed as soon as all portlets have migrated to 
    // the new portlet.xml initialisation parameters
    @Deprecated
    private void performSanityCheck() {

    	if(Strings.isValid(InitParameter.ACTIONS_CONFIGURATION_FILE.getValueForPortlet(this))) {
    		logger.error("please remove deprecated initialisation parameter '{}' from your portlet.xml", 
    				InitParameter.ACTIONS_CONFIGURATION_FILE.getLegacyName());
    	}
    	
    	if(Strings.isValid(InitParameter.ACTIONS_JAVA_PACKAGE.getValueForPortlet(this))) {
    		logger.error("please replace deprecated initialisation parameter '{}' with '{}' in your portlet.xml", 
    				InitParameter.ACTIONS_JAVA_PACKAGE.getLegacyName(),
    				InitParameter.ACTIONS_JAVA_PACKAGES.getName());
    	}
    	
    	if(Strings.isValid(InitParameter.RENDERERS_JAVA_PACKAGE.getValueForPortlet(this))) {
    		logger.error("please replace deprecated initialisation parameter '{}' with '{}' in your portlet.xml", 
    				InitParameter.RENDERERS_JAVA_PACKAGE.getLegacyName(),
    				InitParameter.RENDERERS_JAVA_PACKAGES.getName());
    	}
    	
    	// dump initialisation parameters for debugging purposes
    	for(InitParameter parameter : InitParameter.values()) {
    		logger.info(" + parameter: {}", parameter.toString(this));
    	}    	
    }
    
    private void initialisePortletConfiguration() {
    	String value = InitParameter.ACTIONS_CONFIGURATION.getValueForPortlet(this);
    	if(Strings.isValid(value)) {
    		logger.debug("loading actions' configuration from '{}'", value);
    		InputStream stream = null;
    		try {
	    		URL url = URLFactory.makeURL(value);
	    		if(url != null) {
	    			stream = url.openConnection().getInputStream();
	    			logger.trace("opened stream to actions configuration");
	    			configuration = new Properties();
	    			configuration.load(stream);
	    			configuration.lock();
	    			logger.trace("configuration read");
	    		}
    		} catch(MalformedURLException e) {
    			logger.error("invalid URL '{}' for actions configuration: check parameter '{}' in your portlet.xml", value, InitParameter.ACTIONS_CONFIGURATION.getName());
    		} catch (IOException e) {
    			logger.error("error reading from URL '{}', actions configuration will be unavailable", value);
			} catch (PropertiesException e) {
				logger.error("is you see this error, the code has attempted to fill a locked configuration map", e);
			} finally  {
				if(stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						logger.error("error closing input stream", e);
					}
				}
			}
    	}    	
    }
        
    /**
     * Initialises the current runtime environment, with application server and 
     * portal server specific activities and tasks.
     */
    private void initialiseRuntimeEnvironment() {
    	
		// dump current environment
		Map<String, String> environment = System.getenv();
		StringBuilder buffer = new StringBuilder("runtime environment:\n");
		for(String key : environment.keySet()) {
			buffer.append("\t- '").append(key).append("' = '").append(environment.get(key)).append("'\n");
		}
		logger.trace(buffer.toString());

		logger.trace("initialising runtime environment...");
		
		String value = InitParameter.WEB_CONTAINER_PLUGIN.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.trace("trying to load web container as per user's explicit request: '{}'", value);
			Plugin plugin = PluginManager.loadPlugin(value);
			if(plugin != null) {
				logger.trace("web container '{}' loaded", value);
				this.server = (ApplicationServer) plugin;
			}
		}
		if(this.server == null) {
			// the server plug-in has not been initialised either because there 
			// was no valid value in the WEB_CONTAINER_PLUGIN parameter or the
			// class probing/loading process failed, try with class path
			value = InitParameter.WEB_CONTAINER_PACKAGES.getValueForPortlet(this);
			if(Strings.isValid(value)) {
				logger.trace("using user-provided class paths to load web container plugin: '{}'", value);
			} else {
				value = DEFAULT_CONTAINERS_CLASSPATH;
				logger.trace("using default classpath to load web container plugin: '{}'", value);
			}
			List<Plugin> plugins = PluginManager.loadPluginsInPath(ApplicationServerPluginFactory.class, Strings.split(value, ",", true));
			switch(plugins.size()) {
			case 0:
				logger.warn("no application server plugin found, some functionalities might not be available through ActionContext");
				break;
			case 1:
				logger.trace("exactly one application server plugin found that supports the current environment");
				this.server = (ApplicationServer)plugins.get(0);
				break;
			default:
				logger.warn("more than a single application server plugin found: we're picking the first one, but you may want to check your plugin probes' effectiveness");
				this.server = (ApplicationServer)plugins.get(0);
				break;
			}
		}
		
		value = InitParameter.PORTLET_CONTAINER_PLUGIN.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.trace("trying to load portlet container as per user's explicit request: '{}'", value);
			Plugin plugin = PluginManager.loadPlugin(value);
			if(plugin != null) {
				logger.trace("portlet container '{}' loaded", value);
				this.portal = (PortalServer) plugin;
			}
		}
		if(this.portal == null) {
			// the portal plug-in has not been initialised either because there 
			// was no valid value in the PORTLET_CONTAINER_PLUGIN parameter or the
			// class probing/loading process failed, try with class path
			value = InitParameter.PORTLET_CONTAINER_PACKAGES.getValueForPortlet(this);
			if(Strings.isValid(value)) {
				logger.trace("using user-provided class paths to load portlet container plugin: '{}'", value);
			} else {
				value = DEFAULT_CONTAINERS_CLASSPATH;
				logger.trace("using default classpath to load portlet container plugin: '{}'", value);
			}
			List<Plugin> plugins = PluginManager.loadPluginsInPath(PortalServerPluginFactory.class, Strings.split(value, ",", true));
			switch(plugins.size()) {
			case 0:
				logger.warn("no portal server plugin found, some functionalities might not be available through ActionContext");
				break;
			case 1:
				logger.trace("exactly one portal server plugin found that supports the current environment");
				this.portal = (PortalServer)plugins.get(0);
				break;
			default:
				logger.warn("more than a single portal server plugin found: we're picking the first one, but you may want to check your plugin probes' effectiveness");
				this.portal = (PortalServer)plugins.get(0);
				break;
			}
		}
		logger.trace("runtime initialisation done!");
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
		registry.setHtmlPathInfo(InitParameter.JSP_ROOT_PATH.getValueForPortlet(this), InitParameter.JSP_PATH_PATTERN.getValueForPortlet(this));
		
		// pre-scan existing classes and methods in the default actions package
		TargetFactory loader = new TargetFactory();
		
		String parameter = InitParameter.ACTIONS_JAVA_PACKAGES.getValueForPortlet(this);
		if(Strings.isValid(parameter)) {
			logger.trace("scanning for actions in packages: '{}'", parameter);
			String [] packages = Strings.split(parameter, ",", true);
			for(String pkg : packages) {
				loader.makeFromJavaPackage(registry, pkg);
			}
		} else {
			String pkg = InitParameter.ACTIONS_JAVA_PACKAGE.getValueForPortlet(this);
			if(Strings.isValid(pkg)) {
				logger.warn("attention: using legacy parameter '{}' to specify actions' single package: '{}' (please consider switching to '{}')", 
						InitParameter.ACTIONS_JAVA_PACKAGE.getLegacyName(),
						pkg,
						InitParameter.ACTIONS_JAVA_PACKAGES.getName());
				loader.makeFromJavaPackage(registry, pkg);
			} else {
				logger.error("no Java packages specified for actions: check parameter '{}'", InitParameter.ACTIONS_JAVA_PACKAGES.getName());
				throw new DeploymentException("No Java package specified for actions: check parameter '" + InitParameter.ACTIONS_JAVA_PACKAGES.getName() + "'");
			}
		}
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
		String value = InitParameter.INTERCEPTORS_CONFIGURATION.getValueForPortlet(this);
		if(Strings.isValid(value)) {			
    		logger.debug("loading interceptors' configuration from '{}'", value);
    		InputStream stream = null;
    		try {
	    		URL url = URLFactory.makeURL(value);
	    		if(url != null) {
	    			stream = url.openConnection().getInputStream();	    			
	    			interceptors.loadFromStream(stream);
	    			logger.trace("interceptors stacks:\n{}", interceptors.toString());
	    		}
    		} catch(MalformedURLException e) {
    			logger.error("invalid URL '{}' for actions configuration: check parameter '{}' in your portlet.xml", value, InitParameter.ACTIONS_CONFIGURATION.getName());
    		} catch (IOException e) {
    			logger.error("error reading from URL '{}', actions configuration will be unavailable", value);
			} finally  {
				if(stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						logger.error("error closing input stream", e);
					}
				}
			}			
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
    	
		String parameter = InitParameter.RENDERERS_JAVA_PACKAGES.getValueForPortlet(this);
		if(Strings.isValid(parameter)) {
			logger.trace("scanning for renderers in packages: '{}'", parameter);
			String [] packages = Strings.split(parameter, ",", true);
			for(String pkg : packages) {
				loader.loadFromJavaPackage(renderers, pkg);
			}
		} else {
			String pkg = InitParameter.RENDERERS_JAVA_PACKAGE.getValueForPortlet(this);
			if(Strings.isValid(pkg)) {
				logger.warn("attention: using legacy parameter '{}' to specify renderers' single package: '{}' (please consider switching to '{}')", 
						InitParameter.RENDERERS_JAVA_PACKAGE.getLegacyName(),
						pkg,
						InitParameter.RENDERERS_JAVA_PACKAGES.getName());
				loader.loadFromJavaPackage(renderers, pkg);
			}
		}
		logger.trace("renderers configuration:\n{}", renderers.toString());    	
    }

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in custom XXXX (whatever) mode. This page is the starting point of the custom 
	 * mode HTML navigation tree.
	 */	
	private static final String RENDER_XXXX_HOMEPAGE = "strutlets:xxxx-home";    

	/**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ActionController.class);
}
