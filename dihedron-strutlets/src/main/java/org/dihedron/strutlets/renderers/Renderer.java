/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.renderers;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;


/**
 * The base interface for all renderers.
 * 
 * @author Andrea Funto'
 */
public interface Renderer {
	
	/**
	 * Returns the identifier of the renderer, e.g. "jsp" for the JSP include
	 * renderer.
	 * 
	 * @return
	 *   the id of the renderer.
	 */
	String getId();
	
	/**
	 * Sets a reference to the portlet that will be using this renderer.
	 * 
	 * @param portlet
	 *   a reference to the portlet that will be using this renderer.
	 */
	void setPortlet(GenericPortlet portlet);

	/**
	 * Renders the output to the client.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @param data
	 *   the {@code Renderer}-specific data, e.g. the URL for the JSP renderer,
	 *   the name of a java bean for the JSON and XML renderers, etc. Renderer 
	 *   data can be a JSON string if the renderer requires more complex or
	 *   structured data to perform its work.
	 * @throws IOException
	 *   if it cannot write to the output stream.
	 * @throws PortletException
	 *   if any portlet-specific error occurs during the processing.
	 */
	void render(PortletRequest request, PortletResponse response, String data) throws IOException, PortletException; 
}
