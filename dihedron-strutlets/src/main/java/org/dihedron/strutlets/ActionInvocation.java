/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.dihedron.strutlets.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ActionInvocation {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionInvocation.class);

	/**
	 * The action instance on which the business method is being invoked.
	 */
	private Object action;
	
	/**
	 * The information (metadata) pertaining to the business method being invoked 
	 * on the action instance.
	 */
	private Target target;
	
	/**
	 * The <code>ActionRequest</code>, <code>EventRequest</code> or
	 * <code>RenderRequest</code> object.
	 */
	private PortletRequest request;
	
	/**
	 * The <code>ActionResponse</code>, <code>EventResponse</code> or
	 * <code>RenderResponse</code> object.
	 */
	private PortletResponse response;

	/**
	 * The thread-specific store for the iterator on the list of interceptors.
	 */
	private ThreadLocal<Iterator<Interceptor>> iterator = new ThreadLocal<Iterator<Interceptor>>() {
		@Override protected Iterator<Interceptor> initialValue() {
			return null;
		}
	};
	
	/**
	 * The stack of interceptors.
	 */
	private InterceptorStack interceptors;
	
	/**
	 * Constructor.
	 * 
	 * @param action
	 *   the action instance on which the business method is being invoked.
	 * @param target
	 *   the metadata (information) about the method being invoked.
	 * @param interceptors
	 *   the <code>InterceptorStack</code> representing the set of interceptors 
	 * @param request
	 *   the <code>PortletRequest</code> object.
	 * @param response
	 *   the <code>PortletResponse</code> object.
	 */
	public ActionInvocation(Object action, Target target, InterceptorStack interceptors, 
			PortletRequest request, PortletResponse response) {
		this.action = action;
		this.target = target;
		this.request = request;
		this.response = response;
		this.interceptors = interceptors;
		this.iterator.set(null);
	}
	
	/**
	 * Returns the action being invoked.
	 * 
	 * @return
	 *   the action being invoked.
	 */
	public Object getAction() {
		return action;
	}
	
	/**
	 * Returns the information pertaining to the method being invoked.
	 * 
	 * @return
	 *   the information on the business method being invoked.
	 */
	public Target getTarget() {
		return target;
	}
	
	/**
	 * Returns the current portlet request.
	 * 
	 * @return
	 *   the current portlet request.
	 */
	public PortletRequest getPortletRequest() {
		return request;
	}
	
	/**
	 * Returns the current portlet response.
	 * 
	 * @return
	 *   the current portlet response.
	 */
	public PortletResponse getPortletResponse() {
		return response;
	}
	
	/**
	 * Returns the <code>ActionRequest</code> object.
	 * 
	 * @return
	 *   the <code>ActionRequest</code> object if the caller is handling a 
	 *   <code>processRequest()</code>, null otherwise.
	 */
	public ActionRequest getActionRequest() {
		if(request instanceof ActionRequest) {
			return (ActionRequest)request;
		}
		return null;
	}
	
	/**
	 * Returns the <code>ActionResponse</code> object.
	 * 
	 * @return
	 *   the <code>ActionResponse</code> object if the caller is handling a 
	 *   <code>processRequest()</code>, null otherwise.
	 *   
	 */
	public ActionResponse getActionResponse() {
		if(response instanceof ActionResponse) {
			return (ActionResponse)response;
		}
		return null;
	}
	
	/**
	 * Returns the <code>EventRequest</code> object.
	 * 
	 * @return
	 *   the <code>EventRequest</code> object if the caller is handling a 
	 *   <code>processEvent()</code>, null otherwise.
	 */
	public EventRequest getEventRequest() {
		if(request instanceof EventRequest) {
			return (EventRequest)request;
		}
		return null;
	}
	
	/**
	 * Returns the <code>EventResponse</code> object.
	 * 
	 * @return
	 *   the <code>EventResponse</code> object if the caller is handling a 
	 *   <code>processEvent()</code>, null otherwise.
	 *   
	 */
	public EventResponse getEventResponse() {
		if(response instanceof EventResponse) {
			return (EventResponse)response;
		}
		return null;
	}
	
	/**
	 * Returns the <code>RenderRequest</code> object.
	 * 
	 * @return
	 *   the <code>RenderRequest</code> object if the caller is handling a 
	 *   <code>render()</code>, null otherwise.
	 */
	public RenderRequest getRenderRequest() {
		if(request instanceof RenderRequest) {
			return (RenderRequest)request;
		}
		return null;
	}

	/**
	 * Returns the <code>RenderResponse</code> object.
	 * 
	 * @return
	 *   the <code>RenderResponse</code> object if the caller is handling a 
	 *   <code>render()</code>, null otherwise.
	 */
	public RenderResponse getRenderResponse() {
		if(response instanceof RenderResponse) {
			return (RenderResponse)response;
		}
		return null;
	}

	/**
	 * Returns the <code>ResourceRequest</code> object.
	 * 
	 * @return
	 *   the <code>ResourceRequest</code> object if the caller is handling a 
	 *   <code>serveResource()</code>, null otherwise.
	 */
	public ResourceRequest getResourceRequest() {
		if(request instanceof ResourceRequest) {
			return (ResourceRequest)request;
		}
		return null;
	}

	/**
	 * Returns the <code>ResourceResponse</code> object.
	 * 
	 * @return
	 *   the <code>ResourceResponse</code> object if the caller is handling a 
	 *   <code>serveResource()</code>, null otherwise.
	 */
	public ResourceResponse getResourceResponse() {
		if(response instanceof ResourceResponse) {
			return (ResourceResponse)response;
		}
		return null;
	}
	
	/**
	 * Invokes the next interceptor in the stack, or the action if this
	 * is the last interceptor.
	 * 
	 * @return
	 *   the interceptor result; if the interceptor is not intended to divert 
	 *   control flow, it should pass through whatever results from the nested 
	 *   interceptor call; changing this result with a different value results 
	 *   in a deviation of the workflow.  
	 * @throws StrutletsException
	 */
	public String invoke() throws StrutletsException {
		
		// invoke the interceptors stack
		if(iterator.get() == null) {
			iterator.set(interceptors.iterator());
		}
		if(iterator.get().hasNext()) {
			return iterator.get().next().intercept(this);
		}
		// now invoke the static proxy method 
		try {
			Method proxy = target.getProxyMethod();
			logger.trace("invoking actual method on action instance through proxy '{}'", proxy.getName());
			return (String)proxy.invoke(null, action);
		} catch (IllegalArgumentException e) {
			logger.error("illegal argument to proxy method invocation", e);
			throw new StrutletsException("Illegal argument to proxy method invocation", e);
		} catch (IllegalAccessException e) {
			logger.error("illegal access to proxy method during invocation", e);
			throw new StrutletsException("Illegal access to proxy method during invocation", e);
		} catch (InvocationTargetException e) {
			logger.error("invocation target error calling proxy method", e);
			throw new StrutletsException("Invocation target error calling proxy method", e);
		}
	}
	
	/**
	 * Cleans up after the invocation has completed, by unbinding data from the 
	 * thread-local storage; this method must be called after each invocation,
	 * no matter how it ends, whether in success or with an exception; add it to 
	 * a "finally" block around the action invocation.
	 */
	public void cleanup() {
		logger.debug("removing the interceptors iterator from the thread-local storage");
		iterator.remove();
	}
}
