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

package org.dihedron.strutlets.actions;



/**
 * @author Andrea Funto'
 */
public class Result {
	
	/**
	 * The result identifier (e.g. "error", "success").
	 */
	private String id;
	
	/**
	 * The new mode in which the portlet will be put if this is the action's 
	 * result.
	 */
	private PortletMode mode;
	
	/**
	 * The new state of the portlet's window if this is the action's result. 
	 */
	private WindowState state;
	
	/**
	 * The URL of the JSP or servlet providing the action's view.
	 */
	private String url;
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the result identifier (e.g. "success");
	 * @param mode
	 *   the id of the new mode in which the portlet will be put if this is the 
	 *   action's result.
	 * @param state
	 *   the id of the new state of the portlet's window if this is the action's 
	 *   result.
	 * @param url
	 *   the URL of the JSP or servlet providing the action's view.
	 */
	@Deprecated
	public Result(String id, String mode, String state, String url) {
		this.id = id;
		this.mode = PortletMode.getPortletMode(mode);
		this.state = WindowState.getWindowState(state);		
		this.url = url;
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the result identifier (e.g. "success");
	 * @param mode
	 *   the new mode in which the portlet will be put if this is the action's 
	 *   result.
	 * @param state
	 *   the new state of the portlet's window if this is the action's result.
	 * @param url
	 *   the URL of the JSP or servlet providing the action's view.
	 */
	public Result(String id, PortletMode mode, WindowState state, String url) {
		this.id = id;
		this.state = state;
		this.mode = mode;
		this.url = url;
	}

	/**
	 * Retrieves the result's identifier.
	 * 
	 * @return 
	 *   the result's identifier (e.g. "success").
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retrieves the new mode for the portlet.
	 * 
	 * @return 
	 *   the new portlet mode.
	 */
	public PortletMode getPortletMode() {
		return mode;
	}

	/**
	 * Retrieves the new window state for the portlet.
	 * 
	 * @return
	 *   the new window state for the portlet.
	 */
	public WindowState getWindowState() {
		return state;
	}

	/**
	 * Returns the URL of the JSP or servlet that will provide the actions' view.
	 * 
	 * @return
	 *   the URL of the JSP or servlet that will provide the actions' view.
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Returns a pretty-printed string representation of the object.
	 * 
	 * @return
	 *   a pretty-printed string representation of the object.
	 * @see 
	 *   java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder("result\n");
		builder.append(String.format(" + id     : '%1$s'\n", id));
		builder.append(String.format(" + mode   : '%1$s'\n", mode));
		builder.append(String.format(" + state  : '%1$s'\n", state));		
		builder.append(String.format(" + url    : '%1$s'\n", url));
		return builder.toString();		
	}
}
