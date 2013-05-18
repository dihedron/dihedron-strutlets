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

import javax.portlet.GenericPortlet;

/**
 * The enumeration of supported initialisation parameters.
 * 
 * @author Andrea Funto'
 */
public enum InitParameter {
	/**
	 * The parameter used to specify the path to the actions configuration file;
	 * If no value is specified, the framework will try locale a file called
	 * "actions-config.xml" under the root directory in the classpath. This
	 * parameter can be used to provide a different configuration file.
	 */
	ACTIONS_CONFIGURATION_FILE("actions.configuration.filename"),
	
	/**
	 * The parameter used to specify the default Java package where non-
	 * configured, annotated actions are to be located. This is used only 
	 * when dealing with annotated actions and smart defaults.
	 */
	ACTIONS_JAVA_PACKAGE("actions.java.package"),
	
	/**
	 * The parameter used to override the name of the interceptors stack
	 * configuration XML file; by default it is called "interceptors-config.xml".
	 */
	INTERCEPTORS_CONFIGURATION_FILE("interceptors.configuration.filename"),
	
	/**
	 * The parameter used to override the default interceptors stack to be 
	 * used when invoking non-configured or non-fully-configured actions; by
	 * default it is the "default" stack. 
	 */
	INTERCEPTORS_DEFAULT_STACK("interceptors.default.stack"),
	
	/**
	 * The Java package where custom renderer classes are looked for, if non null.
	 */
	RENDERERS_JAVA_PACKAGE("renderers.java.package"),
	
	/**
	 * The parameter used to specify the root directory for JSP renderers.
	 * This is used only when dealing with annotated actions and smart defaults,
	 * to conjure the name of renderer JSPs based on the action's result and
	 * the current portlet mode.
	 */
	RENDER_ROOT_DIRECTORY("render.root.directory"),
			
	/**
	 * The parameter used to specify the pattern to create the path to JSP 
	 * pages for auto-configured targets. Accepted variables include:<ul>
	 * <li><b>${rootdir}</b>: the root directory, as specified via 
	 * parameter <code>render.root.directory</code>;</li>
	 * <li><b>${action}</b>: the name of the action;<li>
	 * <li><b>${method}</b>: the name of the method;<li>
	 * <li><b>${result}</b>: the result id of the execution, e.g. "success";<li>
	 * <li><b>${mode}</b>: the new portlet mode after the method execution, 
	 * e.g. "maximised";<li>
	 * <li><b>${state}</b>: the new portlet window state after the method 
	 * execution, e.g. "success".<li></ul>
	 */
	RENDER_PATH_PATTERN("render.path.pattern"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in VIEW mode. This page is the starting point of the VIEW mode HTML 
	 * navigation tree.
	 */
	RENDER_VIEW_HOMEPAGE("render.view.homepage"),
	
	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in EDIT mode. This page is the starting point of the EDIT mode HTML 
	 * navigation tree.
	 */
	RENDER_EDIT_HOMEPAGE("render.edit.homepage"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in HELP mode. This page is the starting point of the HELP mode HTML 
	 * navigation tree.
	 */
	RENDER_HELP_HOMEPAGE("render.help.homepage");
			
	/**
	 * Constructor.
	 * 
	 * @param name
	 *   the initialisation parameter name.
	 */
	private InitParameter(String name) {
		this.name = name;
	}
	
	/**
	 * The string representing the name of the initialisation parameter.
	 */
	private String name;
	
	/**
	 * Returns the name of the initialisation parameter.
	 * 
	 * @return
	 *   the name of the initialisation parameter.
	 */
	public String getName() {
		return name;
	}
	
    /**
     * Retrieves the value of the input parameter for the given portlet.
     * 
     * 
     * @param portlet
     *   the portlet whose parameter's value is to be retrieved.
     * @return
     *   the value of the input parameter.
     */
    public String getValueForPortlet(GenericPortlet portlet) {
    	return portlet.getInitParameter(name);
    }	
    
    /**
     * Returns the parameter's name and value as a String.
     * 
     * @param portlet
     *   the portlet whose parameter's name and value is to be printed.
     * @return
     *   the name and value of the input parameter.
     */
    public String toString(GenericPortlet portlet) {
    	return "'" + getName() + "':='" + getValueForPortlet(portlet) + "'";
    }
}
