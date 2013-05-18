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

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dihedron.strutlets.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code}
 * @author Andrea Funto'
 */
@Alias("jsp")
public class JspRenderer extends AbstractRenderer {
	
	public static final String ID = "jsp";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JspRenderer.class);
	
	/**
	 * @see org.dihedron.strutlets.renderers.Renderer#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public void render(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        PortletRequestDispatcher dispatcher = getPortlet().getPortletContext().getRequestDispatcher(response.encodeURL(getData()));

        if (dispatcher == null) {
            logger.error("'{}' is not a valid include path (jsp)", getData());
        } else {
            dispatcher.include(request, response);
        }		
	}
}
