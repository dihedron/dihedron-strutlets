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
import javax.portlet.PortletRequestDispatcher;
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
import org.dihedron.strutlets.actions.Semantics;
import org.dihedron.strutlets.actions.Target;
import org.dihedron.strutlets.actions.factory.ActionFactory;
import org.dihedron.strutlets.configuration.Configuration;
import org.dihedron.strutlets.configuration.ConfigurationLoader;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.dihedron.strutlets.interceptors.factory.InterceptorsFactory;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Strutlets framework controller portlet.
 */
public class ActionController extends GenericPortlet {

	/**
	 * The enumeration of supported initialisation parameters.
	 * 
	 * @author Andrea Funto'
	 */
	public enum InitParameter {
		/**
		 * The parameter used to specify the path to the actions configuration file;
		 * If no value is specified, the framework will try locale a file called
		 * "actions-config.xml" under the root directory in the classpath. This
		 * parameter can be used to provide a different configuration file.
		 */
		ACTIONS_CONFIGURATION_FILE("actions.configuration.filename"),
		
		/**
		 * The parameter used to specify the default Java package where non-
		 * configured, annotated actions are to be located. This is used only 
		 * when dealing with annotated actions and smart defaults.
		 */
		ACTIONS_JAVA_PACKAGE("actions.java.package"),
		
		/**
		 * The parameter used to override the name of the interceptors stack
		 * configuration XML file; by default it is called "interceptors-config.xml".
		 */
		INTERCEPTORS_CONFIGURATION_FILE("interceptors.configuration.filename"),
		
		/**
		 * The parameter used to override the default interceptors stack to be 
		 * used when invoking non-configured or non-fully-configured actions; by
		 * default it is the "default" stack. 
		 */
		INTERCEPTORS_DEFAULT_STACK("interceptors.default.stack"),
		
		/**
		 * The parameter used to specify the root directory for JSP renderers.
		 * This is used only when dealing with annotated actions and smart defaults,
		 * to conjure the name of renderer JSPs based on the action's result and
		 * the current portlet mode.
		 */
		RENDER_ROOT_DIRECTORY("render.root.directory"),
				
		/**
		 * The parameter used to specify the pattern to create the path to JSP 
		 * pages for auto-configured targets. Accepted variables include:<ul>
		 * <li><b>${rootdir}</b>: the root directory, as specified via 
		 * parameter <code>render.root.directory</code>;</li>
		 * <li><b>${action}</b>: the name of the action;<li>
		 * <li><b>${method}</b>: the name of the method;<li>
		 * <li><b>${result}</b>: the result id of the execution, e.g. "success";<li>
		 * <li><b>${mode}</b>: the new portlet mode after the method execution, 
		 * e.g. "maximised";<li>
		 * <li><b>${state}</b>: the new portlet window state after the method 
		 * execution, e.g. "success".<li></ul>
		 */
		RENDER_PATH_PATTERN("render.path.pattern"),

		/**
		 * The parameter used to specify the home page to be used by the framework
		 * in VIEW mode. This page is the starting point of the VIEW mode HTML 
		 * navigation tree.
		 */
		RENDER_VIEW_HOMEPAGE("render.view.homepage"),
		
		/**
		 * The parameter used to specify the home page to be used by the framework
		 * in EDIT mode. This page is the starting point of the EDIT mode HTML 
		 * navigation tree.
		 */
		RENDER_EDIT_HOMEPAGE("render.edit.homepage"),

		/**
		 * The parameter used to specify the home page to be used by the framework
		 * in HELP mode. This page is the starting point of the HELP mode HTML 
		 * navigation tree.
		 */
		RENDER_HELP_HOMEPAGE("render.help.homepage");
				
		/**
		 * Constructor.
		 * 
		 * @param name
		 *   the initialisation parameter name.
		 */
		private InitParameter(String name) {
			this.name = name;
		}
		
		/**
		 * The string representing the name of the initialisation parameter.
		 */
		private String name;
		
		/**
		 * Returns the name of the initialisation parameter.
		 * 
		 * @return
		 *   the name of the initialisation parameter.
		 */
		public String getName() {
			return name;
		}
		
	    /**
	     * Retrieves the value of the input parameter for the given portlet.
	     * 
	     * 
	     * @param portlet
	     *   the portlet whose parameter's value is to be retrieved.
	     * @return
	     *   the value of the input parameter.
	     */
	    public String getValue(GenericPortlet portlet) {
	    	return portlet.getInitParameter(name);
	    }	
	    
	    /**
	     * Returns the parameter's name and value as a String.
	     * 
	     * @param portlet
	     *   the portlet whose parameter's name and value is to be printed.
	     * @return
	     *   the name and value of the input parameter.
	     */
	    public String toString(GenericPortlet portlet) {
	    	return "'" + getName() + "':='" + getValue(portlet) + "'";
	    }
	}
	       
	/**
	 * The default name of the actions configuration file.
	 */
	public static final String DEFAULT_ACTIONS_CONFIG_XML = "actions-config.xml";
	
	/**
	 * The name of the file declaring the default interceptor stack.
	 */
	public static final String DEFAULT_INTERCEPTORS_CONFIG_XML = "org/dihedron/strutlets/default-interceptors-config.xml";

	/**
	 * The name of the default interceptors stack.
	 */
	public static final String DEFAULT_INTERCEPTORS_STACK = "default";
	
	/**
	 * The name of the parameter under which the requested action's name is stored
	 * in the <code>ActionRequest</code> parameters map. 
	 */
	private static final String ACTION_TARGET = ActionRequest.ACTION_NAME;
	
	/**
	 * The parameter used to pass information about the last action/event execution
	 * to the render phase. This parameter is internal to the framework and should 
	 * not be used outside of it (e.g in render URLs). 
	 */
	private static final String EXECUTION_TARGET = "org.dihedron.strutlets.action";
	
	/**
	 * The name of the session attribute under which the action's result is stored.
	 * This parameter is persistent and allows for multiple render request to be 
	 * serviced while keeping information about the latest action's execution status.
	 */
	private static final String EXECUTION_RESULT = "org.dihedron.strutlets.result";
	
	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in custom XXXX (whatever) mode. This page is the starting point of the custom mode 
	 * HTML navigation tree.
	 */	
	private static final String RENDER_XXXX_HOMEPAGE = "render.xxxx.homepage";
	
	/**
	 * This parameter is used by Liferay to create render requests that navigate
	 * directly to the given URL; in order to be compatible with Liferay's
	 * default JSPs, this parameter is checked before redirecting the client
	 * to the default, home page.  
	 */
	private static final String RENDER_REDIRECT_PARAMETER = "jspPage";
	
	/**
	 * The factory of interceptors' stacks.
	 */
	private InterceptorsFactory interceptors;
	
	/**
	 * The actions configuration.
	 */
	private Configuration configuration;

    /**
     * Initialises the controller portlet. The process follows these steps:<ol>
     * <li><b>loading the actions configuration into the single instance of the
     * <code>Configuration</code><b>: this object is responsible for providing
     * relevant information about all the actions, whether explicitly configured 
     * or self-configured through annotations, such as supported methods, which 
     * interceptors stack to apply, and which page (in which mode and which window 
     * state) to apply for each result. The loading process in is two phases, first
     * the initialisation parameter is checked for the name of the file to load,:
     * if provided, the configuration is loaded from there; if not provided, then 
     * the default location (WEB-INF/)is scanned for the existence of a file 
     * named <code>actions-config.xml</code> and if found it is loaded.</li>
     * <li><b>loading the interceptors stacks</b>: the framework already provides 
     * a standard set of interceptors, but the user can provide her own by 
     * setting a value for the <code>???</code> parameter and placing a file with
     * the relevant information under the same path. User stacks override default 
     * stacks if names collide.</li>
     * ... 
     * </ol>
     * 
     * @see javax.portlet.GenericPortlet#init()
     */
    public void init() throws PortletException {
    	super.init();
       
        try {
        	logger.info("   +--------------------------------+   ");
        	logger.info(String.format("   |      STRUTLETS ver. %1$-8s   |   ", Strutlets.VERSION));
        	logger.info("   +--------------------------------+   ");
        	
        	logger.info("action controller '{}' starting up...", getPortletName());
        	
        	// dump initialisation parameters for debugging purposes
        	for(InitParameter parameter : InitParameter.values()) {
        		logger.info(" + parameter: {}", parameter.toString(this));
        	}
            
            // get the actions configuration repository
        	configuration = new Configuration();
                      
            // try to load the actions configuration, in the following order:
            // a. see if there's a user-specified configuration file
            // b. if not, try the default actions-config-xml under the root
            // self-configuring actions will be configured as requests come
			ConfigurationLoader loader = new ConfigurationLoader();
			
			// load the actions configuration
			String file = InitParameter.ACTIONS_CONFIGURATION_FILE.getValue(this);
			if(Strings.isValid(file)) {
				// load the custom configuration
				logger.info("loading actions configuration from custom location: '{}'", file);
				loader.loadFromClassPath(configuration, file);
			} else {
				// load the default			
				logger.info("loading actions configuration from well-known location: '{}'", DEFAULT_ACTIONS_CONFIG_XML);
				loader.loadFromClassPath(configuration, DEFAULT_ACTIONS_CONFIG_XML);
			}
							
			// set the default Java package where self-configuring annotated actions are to be located
			configuration.setDefaultActionPackage(InitParameter.ACTIONS_JAVA_PACKAGE.getValue(this));
			
			// set the root directory for HTML files and JSPs, for auto-configured annotated actions
			configuration.setRootHtmlDirectory(InitParameter.RENDER_ROOT_DIRECTORY.getValue(this));

			// set the pattern for the HTML files and JSP paths, for auto-configured annotated actions
			configuration.setHtmlPathPattern(InitParameter.RENDER_PATH_PATTERN.getValue(this));
			
			// pre-scan existing classes and methods in the default actions package
			loader.loadFromJavaPackage(configuration, InitParameter.ACTIONS_JAVA_PACKAGE.getValue(this));

			logger.trace("actions configuration:\n{}", configuration.toString());
			
			// now initialise the interceptors stacks
			interceptors = new InterceptorsFactory();
			
			// load the default interceptors stack and others
			logger.info("loading default interceptors stacks: '{}'", DEFAULT_INTERCEPTORS_CONFIG_XML);
			interceptors.loadFromClassPath(DEFAULT_INTERCEPTORS_CONFIG_XML);
			logger.trace("pre-configured interceptors stacks:\n{}", interceptors.toString());
			
			// load the custom interceptors configuration
			file = InitParameter.INTERCEPTORS_CONFIGURATION_FILE.getValue(this);
			if(Strings.isValid(file)) {
				logger.info("loading interceptors configuration from custom location: '{}'", file);
				interceptors.loadFromClassPath(file);
				logger.trace("interceptors stacks:\n{}", interceptors.toString());
			}
			
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
     * result string and looks up the appropriate view JSP.
     * 
     *  @param request
     *    the incoming <code>ActionRequest</code> object.
     *  @param response
     *    the <code>ActionResponse</code> object. 
     * @see 
     *   javax.portlet.GenericPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws IOException, PortletException {
    	logger.trace("processing action...");
    	String target = request.getParameter(ACTION_TARGET);
    	doProcess(target, request, response);
    	logger.trace("... action processing done");
    }
    
    /**
     * Extracts action  from JSR-286 events and and dispatches them to the 
     * appropriate handler.
     * This method receives incoming event processing requests and looks up the 
     * appropriate handler in the target map, then propagates the request to the 
     * action through the interceptors stack. Once the processing is complete, 
     * it collects the result string and looks up the appropriate view JSP.
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
    	QName qname = request.getEvent().getQName();
    	String target = configuration.getEventTarget(qname);
    	doProcess(target, request, response);
    	logger.trace("... event processing done");
    }    
    
    /**
     * The method that perform output rendering; it might include some read-only 
     * business logic.
     * 
     * This method performs some action in order to understand whether the render
     * phase is immediately following the processing of an action or an event (in 
     * which case we should not expect any further Target to be invoked), or it
     * is in response to a render request.
     * In the former case, the method will simply identify the JSP page to invoke
     * by accessing the appropriate entry in the configuration and then it will
     * yield control to the JSP for rendering.
     * In the latter case, the method will test if the provided JSP is actually 
     * a target specification (in the usual format "MyAction!myMethod"), and if
     * so it will forward control to the action and only subsequently will it
     * yield to the JSP, again identified according to the target's configuration.
  
     * This method perform all that is necessary to provide proper output in the 
     * render phase. It proceeds as follows:<ol>
     * <li>checks if there is a render request that involves the execution of some 
     * action: in order to check this, it looks for a parameter named 
     * "javax.portlet.action" among those in the render request;</li>
     * <li>if found, it proceeds with the dispatching to the appropriate target;</li>
     * <li>if not found, it checks if the render phase follows an action or event 
     * processing, by testing the EXECUTION_TARGET and EXECUTION_RESULT parameters:
     * if found, then in dispatches to the appropriate JSP page;</li>
     * <li>if the aforementioned parameters are null, then it tests yet another 
     * parameter: it looks for a value in JSP_PAGE, which is the parameter used by
     * LifeRay to dispatch to a predefined page (e.g. to go "back");</li>
     *   
     * then it 
     * @see javax.portlet.GenericPortlet#render(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {

    	String url = null;
    	
    	logger.trace("rendering output...");
    	
    	// retrieve the name of the target; this may be a target specification 
    	// or a plain JSP page URL; if it's a target, execute it and then
    	// proceed to rendering
    	String target = getTarget(request);
    	logger.trace("target is '{}'", target);
    	
    	if(Target.isValidActionTarget(target)) { // (*)
    		
    		logger.trace("target '{}' is an action invocation", target);
	    	
    		// this is a valid target specification, dispatch to the appropriate 
    		// action and method, then retrieve the result's URL	    		
    		String result = doProcess(target, request, response);
    		// get the URL corresponding to the given target and result
    		url = getUrl(target, result);
    	} else {
			// it's not a pure render request, let's check if we are in a render phase AFTER 
			// an action or event processing, in which case the following two parameters 
			// should have been filled by the doProcess and therefore valid    		
    		target = request.getParameter(EXECUTION_TARGET);
    		String result = request.getParameter(EXECUTION_RESULT);
    		if(Strings.isValid(target) && Strings.isValid(result)) {
    			// yes, this is right after a processAction or processEvent,
    			// get the URL for this combination
    			logger.trace("rendering the result of an action invocation: target was '{}', result is '{}'", target, result);
		    	url = getUrl(target, result);
    		} else {
    			// no, this is a plain render request, let's check "jspPage" 
    			// first, to comply with Liferay's worst practices
	    		url = request.getParameter(RENDER_REDIRECT_PARAMETER);
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
    	}

	    	// we're almost done: proceed with the JSP rendering
    	if(Strings.isValid(url)) {
    		logger.info("rendering through URL: '{}'", url);
    		include(url, request, response);
    	} else {
    		logger.error("invalid render URL");
    		throw new StrutletsException("no render URL available");
    	}
    	logger.trace("... output rendering done");
    }
    
    /**
     * Serves a resource.
     * 
     * @see javax.portlet.GenericPortlet#serveResource(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse)
     */
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        super.serveResource(request, response);
    }
    
    /**
     * Processes an action request, both in the action, in the event and in the render phase.
     * 
     * @param target
     *   the requested target (in the form "MyAction!myMethod").
     * @param request
     *   the current request object.
     * @param response
     *   the current response object.
     * @throws PortletException
     */
    protected void doProcess(String target, PortletRequest request, StateAwareResponse response) throws PortletException {
    	try {
			if(Strings.isValid(target)) {
				// invoke method
				logger.debug("invoking target '{}'...", target);    			
	    		String res = invokeTarget(target, request, response);
	    		
	    		// get routing configuration for given result string
	    		Result result = configuration.getTarget(target).getResult(res);
	    		PortletMode mode = result.getPortletMode();
	    		WindowState state = result.getWindowState();
	    		
	    		logger.debug("... target '{}' returned result: '{}', mode: '{}', state: '{}'", target, result.getId(), mode, state);
	
	    		// change portlet mode	    		
	    		if(Strings.isValid(mode.toString()) && request.isPortletModeAllowed(mode)) {
	    			response.setPortletMode(mode);
	    		}
	    		// change window state	    		
	    		if(Strings.isValid(state.toString()) && request.isWindowStateAllowed(state)) {
	    			response.setWindowState(state);
	    		}
	    		// set renderer URL
		    	response.setRenderParameter(EXECUTION_TARGET, target);
		    	response.setRenderParameter(EXECUTION_RESULT, result.getId());	    		
			} else {			
				// action not specified, check the current mode and service the
				// default page, as specified in the initialisation parameters
				logger.trace("target string not specified, blanking target and result in render parameters and serving default page");
		    	response.setRenderParameter(EXECUTION_TARGET, (String)null);
		    	response.setRenderParameter(EXECUTION_RESULT, (String)null);    			
			}
		} catch(PortletException e) {
			logger.error("portlet exception servicing action request: {}", e.getMessage());
			throw e;
		}
		
    }
    
    protected String doProcess(String target, RenderRequest request, RenderResponse response) throws IOException, PortletException {
    	String result = null;
    	try {
    		if(Strings.isValid(target)) {
    			
    			// check that the method is for presentation
    			Target info = configuration.getTarget(target);
    			if(info.getSemantics() != Semantics.PRESENTATION) {
    				throw new PortletException("Trying to invoke business method in render request");
    			}
    			
    			// invoke method
    			logger.debug("invoking target '{}'...", target);    			
	    		result = invokeTarget(target, request, response);	
	    		logger.debug(" ... target '{}' result is '{}'", target, result);
    		}
    		return result;
    	} catch(PortletException e) {
    		logger.error("portlet exception servicing render request: {}", e.getMessage());
    		throw e;
		}
    }    
    
    
    protected String invokeTarget(String target, PortletRequest request, PortletResponse response) throws StrutletsException {
    	Action action = null;
    	String result = null;
    	try {
    		logger.info("invoking target '{}'", target);
    		
    		// check if there's configuration available for the given action
			Target info = configuration.getTarget(target);
			logger.trace("target configuration:\n{}", info.toString());
    		
			// instantiate the action
			action = ActionFactory.makeAction(info);
			if(action != null) {
				logger.info("action '{}' ready", info.getClassName());
			} else {    			 	
				logger.error("no action found for target '{}'", target);
				throw new StrutletsException("no action could be found for target '" + target + "'");
			}
			
			// get the stack for the given action
			InterceptorStack stack = interceptors.getStackOrDefault(info.getInterceptorStackId());

	    	// get action name, interceptor stack and method from interceptors
			String method = Target.getMethodName(target);

	    	ActionInvocation invocation = new ActionInvocation(action, method, stack, request, response);
	    	
	    	// bind the per-thread invocation context to the current request,
	    	// response and invocation objects
	    	ActionContext.bindContext(this, request, response, invocation);
	    	
	    	// fire the action stack invocation
	    	result = invocation.invoke();
	    	
    	} finally {
    		// unbind the invocation context from the thread-loca storage to
    		// prevent memory leanks and complaints by the app-server
    		ActionContext.unbindContext();
    	}
    	return result;
    }

    /**
     * Forwards control to the given JSP or servlet for rendering.
     * 
     * @param path
     *   the path to the component that will provide the appropriate view markup.
     * @param request
     *   the <code>RenderRequest</code> object.
     * @param response
     *   the <code>RenderResponse</code> object.
     * @throws IOException
     * @throws PortletException
     */
    protected void include(String path, RenderRequest request, RenderResponse response) throws IOException, PortletException {

        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(response.encodeURL(path));

        if (dispatcher == null) {
            logger.error("'{}' is not a valid include path", path);
        } else {
            dispatcher.include(request, response);
        }
    }
    
    /**
     * Retrieves the name of the current target from one of the places where it 
     * might have been set by the requester:<ol>
     * <li>in the {@code EXECUTION_TARGET} render parameter, where it might
     * have been set by a prior action execution (see {@code #doProcess(String, 
     * PortletRequest, StateAwareResponse)}; if this parameter is set, then the 
     * current method is being invoked in the following render phase and it 
     * should render the appropriate JSP page according to the given action's 
     * result, as per the {@code EXECUTION_RESULT} render parameter, otherwise 
     * the process goes on</li>
     * <li>in the {@code javax.portlet.action} parameter, which is the 
     * parameter to use to have a render URL invoke some business logic prior
     * to its render phase, e.g. to query a database and then render the query 
     * results</li>
     * <li>if nothing valid is found under the previous parameter, then it checks 
     * if the name of a JSP page was provided in LifeRay's {@code jspPage}
     * parameter: by default it should be used to provide the name of web page 
     * (JSP), but this frameworks extends its use to address a target prior to 
     * the rendering of a web page</li>
     * <li>if nothing valid was found, then the default render URL is extracted for
     * the current portlet mode from the portlet's initialisation parameters; for 
     * details about how this is achieved, look at {@link #getDefaultUrl(PortletMode)}
     * </li></ol>.
     * 
     * @param request
     *   the portlet request.
     * @return
     *   the target of the request.
     */
    private String getTarget(PortletRequest request) {
    	String target = null;
    	if(!Strings.isValid(request.getParameter(EXECUTION_TARGET))) {
    		logger.trace("trying to get target as action...");
	    	target = request.getParameter(ACTION_TARGET);
			logger.trace("target in ACTION_TARGET is '{}'", target);
			if(!Target.isValidActionTarget(target)) {
				logger.trace("trying to get target as redirect parameter...");
				target = request.getParameter(RENDER_REDIRECT_PARAMETER);
				logger.trace("target in RENDER_REDIRECT_PARAMETER is '{}'", target);
				if(!Strings.isValid(target)) {	
		    		PortletMode mode = request.getPortletMode();
		    		logger.trace("getting default target for mode '{}'", mode);
		    		target = getDefaultUrl(mode);
				}
			}
    	} else {
    		logger.trace("target must be derived from prior action execution result");
    	}
		logger.debug("target: '{}'", target);
		return target;
	}
    
    /**
     * Retrieves the render URL for the given target and its result.
     * 
     * @param target
     *   the target.
     * @param result
     *   the target's result.
     * @return
     *   the URL that will provide the view for the given actiontarget's result.
     */
    private String getUrl(String target, String result){
    	String url = null;
    	if(Strings.isValid(target) && Strings.isValid(result)) {	    	
	    	logger.trace("looking for renderer for target '{}' with result '{}'", target, result);	    	
	    	Target info = configuration.getTarget(target);
	    	Result renderer = info.getResult(result);
	    	url = renderer.getUrl();
	    	logger.debug("renderer URL for target '{}' with result '{}' is '{}' (mode '{}', state '{}')", 
	    			target, result, url, renderer.getPortletMode(), renderer.getWindowState());
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
			url = InitParameter.RENDER_VIEW_HOMEPAGE.getValue(this);
		} else if(mode.equals(PortletMode.EDIT)) {
			logger.trace("getting default URL for mode 'edit'");
			url = InitParameter.RENDER_EDIT_HOMEPAGE.getValue(this);
		} else if(mode.equals(PortletMode.HELP)) {
			logger.trace("getting default URL for mode 'help'");
			url = InitParameter.RENDER_HELP_HOMEPAGE.getValue(this);
		} else {
			logger.trace("getting default URL for custom render mode: '{}'", mode);
			String parameter = RENDER_XXXX_HOMEPAGE.replaceFirst("xxxx", mode.toString());
			url = getInitParameter(parameter);
		}
    	return url;
    }

    /**
     * The logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ActionController.class);
}
