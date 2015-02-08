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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Andrea Funto'
 */
@Alias(JsonRenderer.ID)
public class JsonRenderer extends BeanRenderer {
	
	/**
	 * The renderer unique id.
	 */
	public static final String ID = "json";

	/**
	 * The MIME type returned as content type by this renderer.
	 */
	public static final String JSON_MIME_TYPE = "application/json";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JsonRenderer.class);
	
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
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		String json = mapper.writeValueAsString(object);
		logger.trace("JSON object is:\n{}", json);
		
		if(response instanceof MimeResponse) {
			// this works in both RENDER and RESOURCE (AJAX) phases
			((MimeResponse)response).setContentType(JSON_MIME_TYPE);
		}
		getWriter(response).print(json);
        getWriter(response).flush();
        
	}
}
