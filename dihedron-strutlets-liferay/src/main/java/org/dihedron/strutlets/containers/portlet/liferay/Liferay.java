/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
		HttpServletRequest hr = PortalUtil.getHttpServletRequest(request);
		return PortalUtil.getOriginalServletRequest(hr);
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
