/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
	 * A reference to the protlet that is going to be using this renderer.
	 */
	private GenericPortlet portlet;
		
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
