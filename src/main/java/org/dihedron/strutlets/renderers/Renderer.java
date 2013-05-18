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

package org.dihedron.strutlets.renderers;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


/**
 * The base interface for all renderers.
 * 
 * @author Andrea Funto'
 */
public interface Renderer {
	
	/**
	 * returns the identifier of the renderer, e.g. "jsp" for the JSP include
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
	public void setPortlet(GenericPortlet portlet);
	
	/**
	 * Sets the data that helps the {@code Renderer} perform its task.
	 * 
	 * @param data
	 *   the {@code Renderer}-specific data, e.g. the URL for the JSP renderer,
	 *   the name of a java bean for the JSON and XML renderers, etc.
	 */
	void setData(String data);
	
	/**
	 * Renders the output to the client.
	 * 
	 * @param request
	 *   the request object.
	 * @param response
	 *   the response object.
	 * @throws IOException
	 *   if it cannot write to the output stream.
	 * @throws PortletException
	 *   if any portlet-specific error occurs during the processing.
	 */
	void render(RenderRequest request, RenderResponse response) throws IOException, PortletException; 
}
