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
package org.dihedron.strutlets.containers.portlet.liferay61x;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dihedron.strutlets.containers.portlet.PortletContainer;

import com.liferay.portal.util.PortalUtil;

/**
 * @author Andrea Funto'
 */
public class Liferay61x implements PortletContainer {

	@Override
	public String getName() {		
		return "Liferay Community Edition 6.1.x";
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortletContainer#initialise()
	 */
	@Override
	public boolean initialise() {
		// TODO: implement
		return true;
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortletContainer#cleanup()
	 */
	@Override
	public void cleanup() {
		// TODO: implement
	}

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortletContainer#getHTTPServletRequest(org.dihedron.strutlets.containers.portlet.PortletRequest)
	 */
	@Override
	public HttpServletRequest getHTTPServletRequest(PortletRequest request) {
		return PortalUtil.getHttpServletRequest(request);
//		String articleId = request.getParameter("articleId");		return null;
	}
}
