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

package org.dihedron.strutlets;

import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;

import org.dihedron.strutlets.exceptions.InvalidPhaseException;

/**
 * A "render phase specific" view of the portal functionalities.
 * 
 * @author Andrea Funto'
 */
public class PresentationContext extends ActionContextImpl {
	
	/**
	 * Sets the title of the portlet; this method can only be invoked in the render 
	 * phase.
	 * 
	 * @param title
	 *   the new title of the portlet.
	 * @throws InvalidPhaseException 
	 *   if the method is invoked out of the "render" phase.
	 */
	public static void setPortletTitle(String title) throws InvalidPhaseException {
		ActionContextImpl.setPortletTitle(title);
	}
	
	/**
	 * Factory method: creates a new Action URL. NOTE: this method returns a 
	 * valid result only when called in the render phase, otherwise it returns 
	 * null.
	 *  
	 * @return
	 *   returns a new Action URL if invoked in the render phase, returns null 
	 *   otherwise.
	 */
	public static PortletURL createActionURL() {
		return ActionContextImpl.createActionURL();
	}
	
	/**
	 * Factory method: creates a new Render URL. NOTE: this method returns a 
	 * valid result only when called in the render phase, otherwise it returns 
	 * null.
	 *  
	 * @return
	 *   returns a new Render URL if invoked in the render phase, returns null 
	 *   otherwise.
	 */
	public static PortletURL createRenderURL() {
		return ActionContextImpl.createRenderURL();
	}
	
	/**
	 * Factory method: creates a new Resource URL. NOTE: this method returns a 
	 * valid result only when called in the render phase, otherwise it returns 
	 * null.
	 *  
	 * @return
	 *   returns a new Resource URL if invoked in the render phase, returns null 
	 *   otherwise.
	 */
	public static ResourceURL createResourceURL() {
		return ActionContextImpl.createResourceURL();
	}	
	
	/**
	 * Private constructor, to prevent instantiation.
	 */
	private PresentationContext() {
	}
}
