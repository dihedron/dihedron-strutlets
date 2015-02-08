/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.diagnostics;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;


/**
 * The common interface for all last instance error handlers. A error handler is 
 * called when an exception is thrown during any of the processing phases. 
 * Error handlers are expected to handle the error for each of the phases. The 
 * contract for each phase is detailed in the corresponding method documentation.
 * 
 * @author Andrea Funto'
 */
public abstract class ErrorHandler {
	
	/**
	 * A reference to the owning portlet.
	 */
	protected GenericPortlet portlet = null;
	
	/**
	 * Constructor; stores a reference to the owning portlet into the error 
	 * handler; this can be useful as a gateway to to gain access to initialisation 
	 * parameters and other functionalities needed by the handler.
	 *  
	 * @param portlet
	 *   a reference to the owning portlet.
	 */
	protected ErrorHandler(GenericPortlet portlet) {
		this.portlet = portlet;
	}
	
	/**
	 * Removes the reference to the portlet, to avoid memory leaks (cyclic references).
	 */
	public void cleanup() {
		this.portlet = null;
	}
	
	/**
	 * Called when an error occurs in the Action phase; the result should be the 
	 * path to the JSP or the action target used to show a result to the user in 
	 * the following render phase: it will actually replace the action result.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @param error
	 *   the error.
	 * @return
	 *   the path to a JSP to be shown to the user.
	 * @throws PortletException
	 */
	public abstract String onActionPhaseError(ActionRequest request, ActionResponse response, Throwable error) throws PortletException;
	
	/**
	 * Called when an error occurs in the Resource phase; the method is expected 
	 * not to return any result, since event handling is asynchronous and there's 
	 * no-one waiting for any result.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @param error
	 *   the error.
	 * @throws PortletException
	 */
	public abstract void onEventPhaseError(EventRequest request, EventResponse response, Throwable error) throws PortletException;
	
	/**
	 * Called when an error occurs in the Resource phase; this method should send 
	 * a result back to the caller.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @param error
	 *   the error.
	 * @throws PortletException
	 */
	public abstract void onResourcePhaseError(ResourceRequest request, ResourceResponse response, Throwable error) throws PortletException;
	
	/**
	 * Called when an error occurs in the Render phase; this method should do
	 * all the rendering directly on the response object provided.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @param error
	 *   the error.
	 * @return
	 *   the path to a JSP to be shown to the user.
	 * @throws PortletException
	 */
	public abstract String onRenderPhaseError(RenderRequest request, RenderResponse response, Throwable error) throws PortletException;
}
