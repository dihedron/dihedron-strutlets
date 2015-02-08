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


import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.renderers.impl.JspRenderer;





/**
 * @author Andrea Funto'
 */
public class Result {

	/**
	 * The default renderer to be used for results that do not have a renderer
	 * type specified explicitly in the annotations.
	 */
	public static final String DEFAULT_RENDERER_ID = JspRenderer.ID;

	/**
	 * The result identifier (e.g. "error", "success").
	 */
	private String id;
	
	/**
	 * The type of result; if not overridden the default is assumed to be JSP.
	 */
	private String renderer = DEFAULT_RENDERER_ID;
	
	/**
	 * The data used by the renderer; this can be a URL, the name of a field
	 * in the action class or an attribute in the request, portlet o application 
	 * scope, or anything else for custom renderers.
	 */
	private String data;

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
	 * Constructor.
	 * 
	 * @param id
	 *   the result identifier (e.g. "success").
	 * @param renderer
	 *   the renderer of the result; by default it will be the "jsp" renderer.  
	 * @param data
	 *   the data to be used by the renderer, e.g. the URL of the JSP or servlet 
	 *   providing the action's view (for "jsp" renderers), the name of a bean, 
	 *   etc.
	 * @param mode
	 *   the id of the new mode in which the portlet will be put if this is the 
	 *   action's result.
	 * @param state
	 *   the id of the new state of the portlet's window if this is the action's 
	 *   result.
	 */
	@Deprecated
	public Result(String id, String renderer, String data, String mode, String state) {
		this.id = id;
		this.renderer = Strings.isValid(renderer) ? renderer : DEFAULT_RENDERER_ID;
		this.data = data;
		this.mode = PortletMode.fromString(mode);
		this.state = WindowState.fromString(state);		
		
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the result identifier (e.g. "success").
	 * @param renderer
	 *   the renderer of the result; by default it will be the "jsp" renderer.  
	 * @param data
	 *   the data to be used by the renderer, e.g. the URL of the JSP or servlet 
	 *   providing the action's view (for "jsp" renderers), the name of a bean, 
	 *   etc.
	 * @param mode
	 *   the new mode in which the portlet will be put if this is the action's 
	 *   result.
	 * @param state
	 *   the new state of the portlet's window if this is the action's result.
	 */
	public Result(String id, String renderer, String data, PortletMode mode, WindowState state) {
		this.id = id;
		this.renderer = Strings.isValid(renderer) ? renderer : DEFAULT_RENDERER_ID;;
		this.data = data;
		this.state = state;
		this.mode = mode;		
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
	 * Retrieves the renderer of result (JSP, JSON...).
	 * 
	 * @return
	 *   the renderer of the result (JSP, XML, JSON...).
	 */
	public String getRenderer() {
		return renderer;
	}

	/**
	 * Returns the data used by the renderer to return a meaningful result, e.g
	 * the URL of the JSP or servlet that will provide the actions' view for "jsp"
	 * renderers, or the name of the attribute (the bean) to be rendered as JSON 
	 * or XML.
	 * 
	 * @return
	 *   the data to be passed on to the given renderer, e,g, the URL of the JSP 
	 *   or servlet that will provide the actions' view for "jsp" renderers.
	 */
	public String getData() {
		return data;
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
	 * Returns a pretty-printed string representation of the object.
	 * 
	 * @return
	 *   a pretty-printed string representation of the object.
	 * @see 
	 *   java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder("result {\n");
		builder.append("  id      ('").append(id).append("')\n");
		builder.append("  renderer('").append(renderer).append("')\n");
		builder.append("  data    ('").append(data).append("')\n");
		builder.append("  mode    ('").append(mode).append("')\n");
		builder.append("  state   ('").append(state).append("')\n");
		builder.append("}\n");
		return builder.toString();		
	}
}
