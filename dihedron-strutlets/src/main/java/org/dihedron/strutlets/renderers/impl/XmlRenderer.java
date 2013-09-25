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
@Alias("xml")
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
	 * @see org.dihedron.strutlets.renderers.Renderer#render(javax.portlet.PortletRequest, javax.portlet.PortletResponse)
	 */
	@Override
	public void render(PortletRequest request, PortletResponse response) throws IOException, PortletException {
		
		String bean = getData();
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
