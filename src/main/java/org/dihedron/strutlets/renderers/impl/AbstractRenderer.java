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
import java.io.PrintWriter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceResponse;

import org.dihedron.strutlets.renderers.Renderer;

/**
 * Base class for all renderers.
 * 
 * @author Andrea Funto'
 */
public abstract class AbstractRenderer implements Renderer {

	/**
	 * The {@code Renderer}-specific data, the reference to the object or the 
	 * URL to render.
	 */
	private String data;
	
	/**
	 * A reference to the protlet that is going to be using this renderer.
	 */
	private GenericPortlet portlet;
	
	/**
	 * @see org.dihedron.strutlets.renderers.Renderer#setData(java.lang.String)
	 */
	@Override
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * Returns the {@code Renderer}-specific data used to perform the actual
	 * rendering.
	 * 
	 * @return
	 *   the {@code Renderer}-specific data used to perform the actual rendering.
	 */
	protected String getData() {
		return this.data;
	}
	
	/**
	 * Sets a reference to the portlet that is going to be using this renderer.
	 * 
	 * @param portlet  
	 *   the reference to the portlet that is going to be using this renderer.
	 */
	public void setPortlet(GenericPortlet portlet) {
		this.portlet = portlet;
	}
	
	/**
	 * Returns a reference to the portlet that is going to be using this renderer.
	 * 
	 * @return
	 *   a reference to the portlet that is going to be using this renderer.
	 */
	protected GenericPortlet getPortlet() {
		return portlet;
	}
	
	/**
	 * Returns the {@code PrintWriter} associated with the response object.
	 * 
	 * @param response
	 *   the response object.
	 * @return
	 *   the {@code PrintWriter} associated with the response object.
	 * @throws IOException
	 */
	protected PrintWriter getWriter(PortletResponse response) throws IOException{
		PrintWriter writer = null;
		if(response instanceof RenderResponse) {
			writer = ((RenderResponse)response).getWriter();
		} else if(response instanceof ResourceResponse) {
			writer = ((ResourceResponse)response).getWriter();
		}
		return writer;
	}
}
