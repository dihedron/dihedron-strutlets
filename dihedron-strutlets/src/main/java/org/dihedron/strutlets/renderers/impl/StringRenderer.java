/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.renderers.impl;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.dihedron.strutlets.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Alias(StringRenderer.ID)
public class StringRenderer extends BeanRenderer {
	
	/**
	 * The renderer unique id.
	 */
	public static final String ID = "string";

	/**
	 * The MIME type returned as content type by this renderer.
	 */
	public static final String TEXT_MIME_TYPE = "text/plain";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(StringRenderer.class);
	
	/**
	 * @see org.dihedron.strutlets.renderers.Renderer#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/**
	 * @see org.dihedron.strutlets.renderers.Renderer#render(javax.portlet.PortletRequest, javax.portlet.PortletResponse, java.lang.String)
	 */
	@Override
	public void render(PortletRequest request, PortletResponse response, String data) throws IOException, PortletException {
		
		String bean = data;
		logger.trace("rendering bean '{}'", bean);

		Object object = getBean(request, bean);
		String string = "";
		if(object != null) {
			string = object.toString();			
		}
		logger.trace("string is:\n{}", string);
		
		if(response instanceof MimeResponse) {
			// this works in both RENDER and RESOURCE (AJAX) phases
			((MimeResponse)response).setContentType(TEXT_MIME_TYPE);
		}
		getWriter(response).print(string);
        getWriter(response).flush();        
	}
}
