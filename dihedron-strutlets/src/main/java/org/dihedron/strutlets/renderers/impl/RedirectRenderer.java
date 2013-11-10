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

package org.dihedron.strutlets.renderers.impl;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.dihedron.strutlets.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code}
 * @author Andrea Funto'
 */
@Alias(RedirectRenderer.ID)
public class RedirectRenderer extends AbstractRenderer {
	
	/**
	 * The renderer unique id.
	 */
	public static final String ID = "redirect";
	
	/**
	 * The string representing the portlet context; when used in a URL, it will 
	 * be replaced by the current portlet's web context. For instance, the
	 * following URL: <code>${portlet-context}html/my.jsp</code> will be translated
	 * into the URL of the <code>my.jsp</code> in the portlet's file tree.
	 */
	public static final String CONTEXT = "\\$\\{portlet\\-context\\}";
	
	/**
	 * The string representation of the {@code CONTEXT} regular expression, for
	 * internal use only.
	 */
	private static final String CONTEXT_STRING = "${portlet-context}";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RedirectRenderer.class);
	
	/**
	 * @see org.dihedron.strutlets.renderers.Renderer#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public void render(PortletRequest request, PortletResponse response, String data) throws IOException, PortletException {
		if(!(request instanceof ActionRequest)) {
			logger.error("redirect can only be issued in the action phase");
			throw new PortletException("redirect can only be issued in the action phase");
		}
		
		ActionResponse actionResponse = (ActionResponse)response;
		
		String url = data;
		if(url.contains(CONTEXT_STRING)) {
			logger.trace("replacing portlet context in redirect URL: '{}'", url);
			url = url.replaceAll(CONTEXT, ((ActionRequest)request).getContextPath());
		}

		// sending redirect to the redirect JSP
		logger.trace("redirecting to URL: '{}'", url);		
		actionResponse.sendRedirect(url);
	}
}
