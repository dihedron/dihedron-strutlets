/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.renderers.impl;

import java.io.IOException;

import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dihedron.strutlets.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Alias(XmlRenderer.ID)
public class XmlRenderer extends BeanRenderer {
	
	/**
	 * The renderer unique id.
	 */
	public static final String ID = "xml";
	
	/**
	 * The MIME type returned as content type by this renderer.
	 */
	public static final String XML_MIME_TYPE = "text/xml";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(XmlRenderer.class);
	
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
		JAXBContext context;
		try {
			if(response instanceof MimeResponse) {
				// this works in both RENDER and RESOURCE (AJAX) phases
				((MimeResponse)response).setContentType(XML_MIME_TYPE);
			}			
			context = JAXBContext.newInstance("org.dihedron.strutlets");
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(object, getWriter(response)); 
		} catch (JAXBException e) {
			logger.error("error marshalling bean to XML", e);
			throw new PortletException("Error marshalling Java bean to XML", e); 
		}
	}
}
