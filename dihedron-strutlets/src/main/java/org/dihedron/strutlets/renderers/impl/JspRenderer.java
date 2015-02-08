/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.renderers.impl;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;

import org.dihedron.strutlets.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code}
 * @author Andrea Funto'
 */
@Alias(JspRenderer.ID)
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
	public void render(PortletRequest request, PortletResponse response, String data) throws IOException, PortletException {
        PortletRequestDispatcher dispatcher = getPortlet().getPortletContext().getRequestDispatcher(response.encodeURL(data));

        if (dispatcher == null) {
            logger.error("'{}' is not a valid include path (jsp)", data);
        } else {
            dispatcher.include(request, response);
        }		
	}
}
