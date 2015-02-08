/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.portlet;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dihedron.strutlets.plugins.Plugin;

/**
 * @author Andrea Funto'
 */
public interface PortalServer extends Plugin {
	
	/**
	 * Returns the HTTP servlet request object underlying the current portlet
	 * request.
	 * 
	 * @param request
	 *   the current portlet request object.
	 * @return
	 *   the servlet request object.
	 */
	HttpServletRequest getHTTPServletRequest(PortletRequest request);
	
}
