/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

import org.dihedron.core.properties.Properties;
import org.dihedron.core.properties.PropertiesException;
import org.dihedron.core.strings.Strings;
import org.dihedron.core.url.URLFactory;
import org.dihedron.core.variables.EnvironmentValueProvider;
import org.dihedron.core.variables.SystemPropertyValueProvider;
import org.dihedron.core.variables.Variables;
import org.dihedron.strutlets.ActionContext.Scope;
import org.dihedron.strutlets.actions.Result;
import org.dihedron.strutlets.actions.factory.ActionFactory;
import org.dihedron.strutlets.containers.portlet.PortalServer;
import org.dihedron.strutlets.containers.portlet.PortalServerPluginFactory;
import org.dihedron.strutlets.containers.web.ApplicationServer;
import org.dihedron.strutlets.containers.web.ApplicationServerPluginFactory;
import org.dihedron.strutlets.diagnostics.DefaultErrorHandler;
import org.dihedron.strutlets.diagnostics.ErrorHandler;
import org.dihedron.strutlets.exceptions.DeploymentException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.dihedron.strutlets.interceptors.registry.InterceptorsRegistry;
import org.dihedron.strutlets.plugins.Plugin;
import org.dihedron.strutlets.plugins.PluginManager;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.strutlets.renderers.impl.CachingRendererRegistry;
import org.dihedron.strutlets.renderers.impl.JspRenderer;
import org.dihedron.strutlets.renderers.impl.RedirectRenderer;
import org.dihedron.strutlets.renderers.registry.RendererRegistry;
import org.dihedron.strutlets.renderers.registry.RendererRegistryLoader;
import org.dihedron.strutlets.targets.Target;
import org.dihedron.strutlets.targets.TargetId;
import org.dihedron.strutlets.targets.registry.TargetFactory;
import org.dihedron.strutlets.targets.registry.TargetRegistry;
import org.dihedron.strutlets.upload.FileUploadConfiguration;
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
	 * The configuration for file upload handling.
	 */
	private FileUploadConfiguration uploadInfo = null;
	
	/**
	 * The last-resort error handler.
	 */
	private ErrorHandler errorHandler = null;

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
        	logger.info("   |              {} |   ", Strings.padLeft("strutlets ver. " + Strutlets.getVersion(), 45));
        	logger.info(              "   +------------------------------------------------------------+   ");        	
        	
        	logger.info("action controller '{}' starting up...", getPortletName());
        	        	
        	performSanityCheck();
        	
        	initialisePortletConfiguration();
        	
        	initialiseRuntimeEnvironment();
        	
        	initialiseFileUploadConfiguration();
        	
        	initialiseErrorHandler();

        	initialiseTargetsRegistry();
			
        	initialiseInterceptorsRegistry();
        	
        	initialiseRenderersRegistry();
        	
        	initialiseAdminConsole();
        	
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
    	if(errorHandler != null) {
    		logger.trace("... cleaning up error handler");
    		errorHandler.cleanup();
    	}
    }
    
    /**
     * Returns the current configuration; it can be used to read, update or
     * drop values.
     * 
     * @return
     *   the current configuration.
     */
    public Properties getConfiguration() {
    	return configuration;
    }
    
    /**
     * Returns the current targets registry; it can be used to nable/disable some 
     * targets or to change some wirings.
     * 
     * @return
     *   the current targets registry.
     */
    public TargetRegistry getTargetRegistry() {
    	return registry;
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
	    	ActionContext.bindContext(this, request, response, configuration, server, portal, uploadInfo);
	    	

	    	logger.trace("processing action...");
	    		    	
	    	// check if the target is contained in the request
	    	TargetId targetId = TargetId.makeFromRequest(request);
	    	
	    	String result = null;
	    	
	    	// TODO: if the target is NOT idempotent, then we must check if there
	    	// is already the result of processing the same form in a custom field
	    	// on portlet scope; if so, then we simply return it without doing
	    	// anything, because this is a double submit; if not so, then we reset
	    	// all request parameters (unfortunately we have no control over 
	    	// render parameters, though) and let the action process its input.
	    	// TODO: ActionContext.clearRequestAttributes must be moved down here
	    	
	    	// TODO: the check should be on timestamp and target id!
	    	Target target = this.registry.getTarget(targetId);
	    	String [] timestamps = (String[])ActionContext.getParameterValues(Strutlets.STRUTLETS_FORM_TIMESTAMP);
	    	if(target.isCacheable()) {	    			    		
	    		if(timestamps != null && timestamps.length > 0) {
	    			String timestamp = (String)ActionContext.getAttribute(Strutlets.STRUTLETS_LAST_FORM_TIMESTAMP, Scope.REQUEST);
	    			if(timestamp != null && timestamps[0] != null && timestamp.equalsIgnoreCase(timestamps[0])) {
	    				result = (String)ActionContext.getAttribute(Strutlets.STRUTLETS_LAST_FORM_RESULT, Scope.REQUEST); 
	    				logger.warn("a form with timestamp {} has already been submitted, with result {}", timestamp, result);
	    			}
	    		}
	    	}
	    	
	    	if(result == null) {
		    	// request attributes are removed upon a brand new action request 
		    	ActionContext.clearRequestAttributes();
		    	
	    		result = invokeBusinessLogic(targetId, request, response);
	    		if(target.isCacheable() && timestamps != null && timestamps.length > 0) {
	    			ActionContext.setAttribute(Strutlets.STRUTLETS_LAST_FORM_TIMESTAMP, timestamps[0], Scope.REQUEST);
	    			ActionContext.setAttribute(Strutlets.STRUTLETS_LAST_FORM_RESULT, result, Scope.REQUEST);
	    		}
	    	}
	    	
	    	if(result != null) {
	    		logger.trace("... action processing done with result '{}'", result);
	    	} else {
	    		logger.trace("... action processing ended with redirection to a new page");
	    	}
    	} catch(Throwable e) {
    		logger.error("error caught in the action phase, invoking the error handler...");
    		// handle error and then register the JSP that will render information
    		// into the render parameters, for the following render phase
    		String jsp = errorHandler.onActionPhaseError(request, response, e);
    		ActionContext.setRenderParameter(Strutlets.STRUTLETS_ERROR_JSP, jsp);
    		logger.error("... done with error handling, rendering error page '{}'", jsp);
		} finally {			
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContext.unbindContext();    			
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
	    	ActionContext.bindContext(this, request, response, configuration, server, portal, uploadInfo);
    		
	    	// request attributes are removed upon a brand new event request
	    	ActionContext.clearRequestAttributes();
    	
	    	logger.trace("processing event...");
	    	
	    	// get the name of the event and re-map it onto the target through the registry
	    	QName qname = request.getEvent().getQName();
	    	TargetId targetId = registry.getEventTarget(qname);
	    	
	    	String result = invokeBusinessLogic(targetId, request, response);
	    	
	    	if(result != null) {
	    		logger.trace("... event processing done with result '{}'", result);
	    	} else {
	    		logger.error("result should never be null in the event phase: this is likely to be a bug in the framework");
	    	}
    	} catch(Throwable e) {
    		logger.error("error caught in the event phase, invoking the error handler...");
    		errorHandler.onEventPhaseError(request, response, e);
    		logger.error("... done with error handling");    		    		
    	} finally {
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContext.unbindContext();
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
    		ActionContext.bindContext(this, request, response, configuration, server, portal, uploadInfo);    		
    		
	    	TargetId targetId = null;
	    	Renderer renderer = null;
	    	
	    	// first check if there was an error in the previous Action phase, and 
	    	// if so (the STRUTLETS_ERROR_JSP render parameter is set), forward to
	    	// the indicated JSP.
	    	String jsp = request.getParameter(Strutlets.STRUTLETS_ERROR_JSP);
	    	if(Strings.isValid(jsp)) {
				logger.trace("there was an error in the previous Action phase, forwarding error to error page '{}'", jsp);
				renderers.getRenderer(JspRenderer.ID).render(request, response, jsp);
				return;
	    	}
	    	
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
    			renderer.render(request, response, url);
	    	} else {
	    		logger.error("invalid render URL");
	    		throw new StrutletsException("No valid render URL available");
	    	}    	
	    	logger.trace("... output rendering done");
    	} catch(Throwable e) {
    		logger.error("error caught in the render phase, invoking the error handler...");
    		String jsp = errorHandler.onRenderPhaseError(request, response, e);
    		logger.error("... done with error handling, rendering error page '{}'", jsp);
			if(Strings.isValid(jsp)) {
				logger.trace("forwarding error to default error page");
				Renderer renderer = renderers.getRenderer(JspRenderer.ID);
				if(renderer != null) {
					renderer.render(request, response, jsp);
				}
			} else {
				logger.trace("no last-resort error handling page, rethrowing...");
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				} else if(e instanceof PortletException) {
					throw (PortletException) e;
				} else if(e instanceof IOException) {
					throw (IOException)e;
				}
			}
//    	} catch(PortletException e) {
//			logger.error("error processing render phase");
//			String errorPage = InitParameter.DEFAULT_ERROR_JSP.getValueForPortlet(this);
//			if(Strings.isValid(errorPage)) {
//				logger.trace("forwarding error to default error page");
//				Renderer renderer = renderers.getRenderer(JspRenderer.ID);
//				renderer.render(request, response, errorPage);
//			} else {
//				logger.trace("no last-resort error handling page, rethrowing...");
//				throw e;
//			}
    	} finally {
    		
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContext.unbindContext();		    		
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
    		ActionContext.bindContext(this, request, response, configuration, server, portal, uploadInfo);
	    	
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
	        	renderer.render(request, response, result.getData());
	        	
	        	logger.trace("... output rendering done");
	    		
	    	} else {
	    		logger.trace("... leaving the resource request to the portlet container");
	    		super.serveResource(request, response);
	    	}
    	} catch(Throwable e) {
    		logger.error("error caught in the resource phase, invoking the error handler...");
    		errorHandler.onResourcePhaseError(request, response, e);
    		logger.error("... done with error handling");
    	} finally {    		
    		// unbind the invocation context from the thread-local storage to
    		// prevent memory leaks and complaints by the application server
			logger.trace("unbinding context from thread-local storage");
			ActionContext.unbindContext();
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
     * @throws IOException 
     */
    protected String invokeBusinessLogic(TargetId targetId, PortletRequest request, StateAwareResponse response) throws PortletException, IOException {
    	String res = null;
    	
//    	try {
		if(targetId != null) {
			// invoke method
			logger.debug("invoking target '{}'...", targetId);    			
    		res = invokeTarget(targetId, request, response);
    		
    		// get routing configuration for given result string
    		Result result = registry.getTarget(targetId).getResult(res);
    		
	    	// now, if we have a result that must be handled by a "redirect"
	    	// renderer, we must do it now, before we move on to the render
	    	// phase, so let's check
	    	if(result.getRenderer().equals(RedirectRenderer.ID)) {
	    		if(ActionContext.isActionPhase()) {
	    			if(ActionContext.hasChangedRenderParameters()) {
	    				logger.error("cannot redirect: some render parameter has been changed by the action prior to redirecting.");
	    				throw new StrutletsException("Trying to redirect to another page after having changed the render parameters");
	    			} else {
	    	    		String url = result.getData();
	    	    		logger.trace("redirecting to '{}' right after action execution...", url);
	    	    		Renderer renderer = renderers.getRenderer(RedirectRenderer.ID);
	        			renderer.render(request, response, url);
	    	    		logger.trace("redirect complete");
	    	    		return null;
	    			}
	    		} else {
	    			logger.error("trying to redirect to another page when not in action phase");
	    			throw new StrutletsException("Trying to redirect to another page when not in action phase");
	    		}
	    	}
    		
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
	    	ActionContext.setRenderParameter(Strutlets.STRUTLETS_TARGET, targetId.toString());
	    	ActionContext.setRenderParameter(Strutlets.STRUTLETS_RESULT, result.getId());	    		
		} else {			
			// action not specified, check the current mode and service the
			// default page, as specified in the initialisation parameters
			logger.trace("target not specified, blanking target and result in render parameters and serving default page");
	    	ActionContext.setRenderParameter(Strutlets.STRUTLETS_TARGET, (String)null);
	    	ActionContext.setRenderParameter(Strutlets.STRUTLETS_RESULT, (String)null);    			
		}
//		} catch(PortletException e) {
//			logger.error("portlet exception servicing action request: {}", e.getMessage());
//			throw e;
//		}    	
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
		ActionInvocation invocation = null;
		try {
			invocation = new ActionInvocation(action, target, stack, request, response);
			return invocation.invoke();
		} finally {
			invocation.cleanup();
		}
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
    		
    		// replace system properties or environment variables (if any)
    		logger.trace("replacing variables in actions' configuration property: '{}'", value);
    		value = Variables.replaceVariables(value, new SystemPropertyValueProvider(), new EnvironmentValueProvider());
    		
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
		
		boolean generateValidationCode = false;
		String value = InitParameter.ACTIONS_ENABLE_VALIDATION.getValueForPortlet(this);
		if(Strings.isValid(value) && value.equalsIgnoreCase("true")) {
			logger.info("enabling JSR-349 bean validation code generation");
			generateValidationCode = true;
		} else {
			logger.info("JSR-349 bean validation code generation will be disabled");
			generateValidationCode = false;
		}
		
		String parameter = InitParameter.ACTIONS_JAVA_PACKAGES.getValueForPortlet(this);
		if(Strings.isValid(parameter)) {
			logger.trace("scanning for actions in packages: '{}'", parameter);
			String [] packages = Strings.split(parameter, ",", true);
			for(String pkg : packages) {
				loader.makeFromJavaPackage(registry, pkg, generateValidationCode);
			}
		} else {
			String pkg = InitParameter.ACTIONS_JAVA_PACKAGE.getValueForPortlet(this);
			if(Strings.isValid(pkg)) {
				logger.warn("attention: using legacy parameter '{}' to specify actions' single package: '{}' (please consider switching to '{}')", 
						InitParameter.ACTIONS_JAVA_PACKAGE.getLegacyName(),
						pkg,
						InitParameter.ACTIONS_JAVA_PACKAGES.getName());
				loader.makeFromJavaPackage(registry, pkg, generateValidationCode);
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
		interceptors.load(InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		logger.trace("pre-configured interceptors stacks:\n{}", interceptors.toString());
		
		// load the custom interceptors configuration
		String value = InitParameter.INTERCEPTORS_DECLARATION.getValueForPortlet(this);
		if(Strings.isValid(value)) {			
//    		logger.debug("loading interceptors' configuration from '{}'", value);
    		interceptors.load(value);
    		/*
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
			*/			
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
	 * Initialises support for file uploads.
	 * 
	 * @throws StrutletsException
	 *   if it cannot create or access the uploaded files repository.
	 */
	private void initialiseFileUploadConfiguration() throws StrutletsException {
			
		this.uploadInfo = new FileUploadConfiguration();
		
		// initialise uploaded files repository		
		File repository = null;
		String value = InitParameter.UPLOADED_FILES_DIRECTORY.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.info("using user-provided upload directory: '{}'", value);
			repository  = new File(value);
			if(!repository.exists()) {
				if(repository.mkdirs()) {
					logger.info("directory tree created under '{}'", repository.getAbsolutePath());
				} else {
					logger.error("cannot create directory tree for uploaded files: '{}'", repository.getAbsolutePath());
					throw new DeploymentException("Error creating file upload directory under path '" + repository.getAbsolutePath() + "'");
				}
			}
			
			if(!repository.isDirectory()) {
				logger.error("filesystem object {} is not a directory", repository.getAbsolutePath());
				throw new DeploymentException("File system object at path '" + repository.getAbsolutePath() + "' is not a directory");
			}			
		} else {
			// section PLT.10.3 of the portlet 2.0 specification (on page 67) specifies 
			// that portlet should have an exact correspondence with servlets with respect
			// to the way one obtains the path to the server temporary directory, only 
			// through the PortletContext insetad of the servlet context
			repository = (File)this.getPortletContext().getAttribute(Constants.PORTLETS_TEMP_DIR_ATTRIBUTE);
			logger.info("using application-server upload directory: '{}'", repository.getAbsolutePath());			
		}
				
		// check if directory is writable
		if(!repository.canWrite()) {
			logger.warn("upload directory {} is not writable, using system temporary directory instead!", repository.getAbsolutePath());
			
			String systemTempDir = System.getenv("TMP");
			if(!Strings.isValid(systemTempDir)) {
				systemTempDir = System.getenv("TEMP");				
			}
			if(!Strings.isValid(systemTempDir)) {
				systemTempDir = System.getProperty("java.io.tmpdir");
			}
			if(!Strings.isValid(systemTempDir)) {
				systemTempDir = System.getProperty(Strutlets.STRUTLETS_UPLOAD_DIR);
			}			
			if(!Strings.isValid(systemTempDir)) {
				throw new DeploymentException("No temporary directory for uploading files found on system: you can specify it via environment variables TMP or TEMP, or system property '" + Strutlets.STRUTLETS_UPLOAD_DIR + "'");
			}
			repository = new File(systemTempDir);
			if(!repository.canWrite()) {
				throw new DeploymentException("Directory at path '" + repository.getAbsolutePath() + "' is not writable");
			}
		}
		
		// remove all pre-existing files (commented code is for Java7+)
//		try {
			logger.trace("removing all existing files from directory '{}'...", repository.getAbsolutePath());
			for(File file : repository.listFiles()) {
				logger.trace("...removing '{}'", file);
				file.delete();				
			}
//			DirectoryStream<Path> files = Files.newDirectoryStream(repository.toPath());
//			for(Path file : files) {
//				logger.trace("...removing '{}'", file);
//				file.toFile().delete();
//			}
//		} catch(IOException e) {
//			logger.warn("error deleting all files from upload directory", e);
//		}
		
		logger.info("upload directory '{}' ready", repository.getAbsolutePath());
		
		this.uploadInfo.setRepository(repository);
		
		// initialise maximum uploadable file size per single file
		value = InitParameter.UPLOADED_FILES_MAX_FILE_SIZE.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.trace("setting maximum uploadable size to {}", value);
			this.uploadInfo.setMaxUploadFileSize(Long.parseLong(value));
		} else {
			logger.trace("using default value for maximum uploadable file size: {}", FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_SINGLE);
			this.uploadInfo.setMaxUploadFileSize(FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_SINGLE);
		}
		
		// initialise maximum total uploadable file size
		value = InitParameter.UPLOADED_FILES_MAX_REQUEST_SIZE.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.trace("setting maximum total uploadable size to {}", value);
			this.uploadInfo.setMaxUploadTotalSize(Long.parseLong(value));
		} else {
			logger.trace("using default value for maximum total uploadable size: {}", FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_TOTAL);
			this.uploadInfo.setMaxUploadTotalSize(FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_TOTAL);
		}

		// initialise small file threshold
		value = InitParameter.UPLOADED_SMALL_FILE_SIZE_THRESHOLD.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			logger.trace("setting small files size threshold to {}", value);
			this.uploadInfo.setInMemorySizeThreshold(Integer.parseInt(value));
		} else {
			logger.trace("using default value for small files size threshold: {}", FileUploadConfiguration.DEFAULT_SMALL_FILE_SIZE_THRESHOLD);
			this.uploadInfo.setInMemorySizeThreshold(FileUploadConfiguration.DEFAULT_SMALL_FILE_SIZE_THRESHOLD);
		}
		logger.trace("done configuring file upload support");
	}
	
	/**
	 * Initialises the error handler with the user-provided class or the default
	 * error handler class if none provided.
	 */
	private void initialiseErrorHandler() {
		String value = InitParameter.ERROR_HANDLER_CLASS.getValueForPortlet(this);
		if(Strings.isValid(value)) {
			try {
				logger.info("initialising error handler of class '{}'...", value);
				Class<?> clazz = Class.forName(value.trim());
				@SuppressWarnings("unchecked")
				Constructor<ErrorHandler> constructor = (Constructor<ErrorHandler>) clazz.getConstructor(GenericPortlet.class);
				this.errorHandler = constructor.newInstance(this);
				logger.info("... error handler of class '{}' ready", value);
			} catch (ClassNotFoundException e) {
				logger.error("class '" + value + "' not found on classpath", e);
			} catch (InstantiationException e) {
				logger.error("error instantiating error handler of class '" + value + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("illegal access to error handler class '" + value + "'", e);
			} catch (NoSuchMethodException e) {
				logger.error("no proper constructor with single GenericPortlet parameter found on class '" + value + "'", e);
			} catch (SecurityException e) {
				logger.error("security error accessing constructor of class '" + value + "'", e);
			} catch (IllegalArgumentException e) {
				logger.error("invalid parameter to error handler constructor of class '" + value + "'", e);
			} catch (InvocationTargetException e) {
				logger.error("error invoking constructor of class '" + value + "'", e);
			} finally {
				if(this.errorHandler == null) {
					logger.info("using default error handler");
					this.errorHandler = new DefaultErrorHandler(this);
				}
			}
		} else {
			logger.info("using default error handler");
			this.errorHandler = new DefaultErrorHandler(this);
		}
	}
	
	private void initialiseAdminConsole() throws StrutletsException {
		String value = InitParameter.ENABLE_ADMIN_CONSOLE.getValueForPortlet(this);
		if(Strings.isValid(value) && value.equalsIgnoreCase("true")) {
			
			logger.info("activating the administrative console...");
			
			// scan admin console actions
			TargetFactory loader = new TargetFactory();
			
			boolean generateValidationCode = false;
			value = InitParameter.ACTIONS_ENABLE_VALIDATION.getValueForPortlet(this);
			if(Strings.isValid(value) && value.equalsIgnoreCase("true")) {
				logger.info("enabling JSR-349 bean validation code generation for administrative console");
				generateValidationCode = true;
			} else {
				logger.info("JSR-349 bean validation code generation will be disabled for administrative console");
				generateValidationCode = false;
			}
			
			loader.makeFromJavaPackage(registry, "org.dihedron.strutlets.adminconsole", generateValidationCode);

			logger.trace("... administrative console loaded");    	
			
		}
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
