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
package org.dihedron.strutlets.containers.portlet.liferay;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dihedron.strutlets.containers.portlet.PortalServer;

import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.util.PortalUtil;

/**
 * @author Andrea Funto'
 */
public abstract class Liferay implements PortalServer {

	/**
	 * @see org.dihedron.strutlets.containers.portlet.PortalServer#getHTTPServletRequest(org.dihedron.strutlets.containers.portlet.PortletRequest)
	 */
	@Override
	public HttpServletRequest getHTTPServletRequest(PortletRequest request) {
		return PortalUtil.getHttpServletRequest(request);
	}
	
	/**
	 * Returns a string describing the name and version of this Liferay instance.
	 * 
	 * @return
	 *   a string describing the name and version of this Liferay instance.
	 */
	public String getServerInfo() {
		return ReleaseInfo.getServerInfo();		
	}
	
	/**
	 * Returns a string describing the version of the current Liferay instance.
	 * 
	 * @return
	 *   a string describing the version of the current Liferay instance.
	 */
	public String getServerVersion() {
		return ReleaseInfo.getVersion();
	}
}
