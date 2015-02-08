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

import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.ActionContext.Scope;
import org.dihedron.strutlets.InitParameter;
import org.dihedron.strutlets.Strutlets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class DefaultErrorHandler extends ErrorHandler {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DefaultErrorHandler.class);
	
	private static final String INTERNAL_ERROR_JSP = "/strutlets/error.jsp";

	/**
	 * The error JSP, the page shown to the user when an error occurs.
	 */
	private String errorJsp = null;
	
	/**
	 * Constructor.
	 * 
	 * @param portlet
	 *   the portlet that will use this error handler, and will supply the default
	 *   error JSP address.
	 */
	public DefaultErrorHandler(GenericPortlet portlet) {
		super(portlet);
		errorJsp = InitParameter.ERROR_JSP_PATH.getValueForPortlet(portlet);
		if(!Strings.isValid(errorJsp)) {
			errorJsp = INTERNAL_ERROR_JSP;
		}
		logger.info("error JSP page is '{}'", errorJsp);
	}

	/**
	 * @see org.dihedron.strutlets.diagnostics.ErrorHandler#onActionPhaseError(javax.portlet.ActionRequest, javax.portlet.ActionResponse, java.lang.Throwable)
	 */
	@Override
	public String onActionPhaseError(ActionRequest request, ActionResponse response, Throwable error) throws PortletException {
		ActionContext.setAttribute(Strutlets.STRUTLETS_ERROR_INFO, new Error(error), Scope.REQUEST);
		return errorJsp;
	}

	/**
	 * @see org.dihedron.strutlets.diagnostics.ErrorHandler#onEventPhaseError(javax.portlet.EventRequest, javax.portlet.EventResponse, java.lang.Throwable)
	 */
	@Override
	public void onEventPhaseError(EventRequest request, EventResponse response, Throwable error) throws PortletException {
		// do nothing
	}

	/**
	 * @see org.dihedron.strutlets.diagnostics.ErrorHandler#onResourcePhaseError(javax.portlet.ResourceRequest, javax.portlet.ResourceResponse, java.lang.Throwable)
	 */
	@Override
	public void onResourcePhaseError(ResourceRequest request, ResourceResponse response, Throwable error) throws PortletException {
		// do nothing
	}

	/**
	 * @see org.dihedron.strutlets.diagnostics.ErrorHandler#onRenderPhaseError(javax.portlet.RenderRequest, javax.portlet.RenderResponse, java.lang.Throwable)
	 */
	@Override
	public String onRenderPhaseError(RenderRequest request, RenderResponse response, Throwable error) throws PortletException {
		logger.trace("handling an exception of class {}", error.getClass().getName());
		ActionContext.setAttribute(Strutlets.STRUTLETS_ERROR_INFO, new Error(error), Scope.REQUEST);
		return errorJsp;
//		ErrorInfo info = new ErrorInfo(error);
//		PrintWriter writer;
//		try {
//			writer = response.getWriter();
//			writer.print("<html>");
//			writer.print("<head><title>ERROR</title></head>");
//			writer.print("<body>");
//			writer.print("<p>Internal error: " + info.getMessage() + "</p><br/>");
//			writer.print("<pre>" + info.getStackTrace() + "</pre><br/>");
//			writer.print("</body>");
//			writer.print("</html>");
//		} catch (IOException e) {
//			logger.error("fatal eror writing to output stream", e);
//		}
	}
	
//	/**
//	 * Returns the {@code PrintWriter} associated with the response object.
//	 * 
//	 * @param response
//	 *   the response object.
//	 * @return
//	 *   the {@code PrintWriter} associated with the response object.
//	 * @throws IOException
//	 */
//	protected PrintWriter getWriter(PortletResponse response) throws IOException{
//		PrintWriter writer = null;
//		if(response instanceof RenderResponse) {
//			writer = ((RenderResponse)response).getWriter();
//		} else if(response instanceof ResourceResponse) {
//			writer = ((ResourceResponse)response).getWriter();
//		}
//		return writer;
//	}	
}
