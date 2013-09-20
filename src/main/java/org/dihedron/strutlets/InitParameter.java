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

import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	@Deprecated
	ACTIONS_CONFIGURATION_FILE("", "actions.configuration.filename"),
	
	/**
	 * The parameter used to specify the default Java package where non-
	 * configured, annotated actions are to be located. This is used only 
	 * when dealing with annotated actions and smart defaults.
	 */
	@Deprecated
	ACTIONS_JAVA_PACKAGE("", "actions.java.package"),
	
	/**
	 * The parameter used to specify the comma-separated list of Java packages 
	 * where actions are to be located. Each of these packages will be scanned 
	 * for <code>@Action</code>-annotated classes. 
	 */
	ACTIONS_JAVA_PACKAGES("strutlets:actions-packages", ""),	
	
	/**
	 * The parameter used to override the name of the interceptors stack
	 * configuration XML file; by default it is called "interceptors-config.xml".
	 */
	INTERCEPTORS_CONFIGURATION_FILE("strutlets:interceptors-configuration", "interceptors.configuration.filename"),
	
	/**
	 * The parameter used to override the default interceptors stack to be 
	 * used when invoking non-configured or non-fully-configured actions; by
	 * default it is the "default" stack. 
	 */
	INTERCEPTORS_DEFAULT_STACK("strutlets:interceptors-default-stack", "interceptors.default.stack"),
	
	/**
	 * The Java package where custom renderer classes are looked for, if non null.
	 */
	@Deprecated
	RENDERERS_JAVA_PACKAGE("", "renderers.java.package"),

	/**
	 * The comma-separated list of Java packages where custom renderer classes 
	 * are looked for, if non null.
	 */
	RENDERERS_JAVA_PACKAGES("strutlets:renderers-packages", ""),
	
	/**
	 * The parameter used to specify the root directory for JSP renderers.
	 * This is used only when dealing with annotated actions and smart defaults,
	 * to conjure the name of renderer JSPs based on the action's result and
	 * the current portlet mode.
	 */
	RENDER_ROOT_DIRECTORY("strutlets:render-root-directory", "render.root.directory"),
			
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
	RENDER_PATH_PATTERN("strutlets:render-path-pattern", "render.path.pattern"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in VIEW mode. This page is the starting point of the VIEW mode HTML 
	 * navigation tree.
	 */
	RENDER_VIEW_HOMEPAGE("strutlets:view-home", "render.view.homepage"),
	
	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in EDIT mode. This page is the starting point of the EDIT mode HTML 
	 * navigation tree.
	 */
	RENDER_EDIT_HOMEPAGE("strutlets:edit-home", "render.edit.homepage"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in HELP mode. This page is the starting point of the HELP mode HTML 
	 * navigation tree.
	 */
	RENDER_HELP_HOMEPAGE("strutlets:help-home", "render.help.homepage"),
	
	/**
	 * The parameter used to specify an optional per-application-server plugin,
	 * which will be used by the framework to retrieve platform-specific data.
	 */
	WEB_CONTAINER_PLUGIN("strutlets:web-container-plugin", "web.container.plugin"),
	
	/**
	 * The parameter used to specify an optional per-portlet-container plugin,
	 * which will be used by the framework to retrieve platform-specific data.
	 */
	PORTLET_CONTAINER_PLUGIN("strutlets:portlet-container-plugin", "portlet.container.plugin");
			
	/**
	 * Constructor.
	 * 
	 * @param name
	 *   the initialisation parameter name.
	 * @param legacyName
	 *   the legacy name of the initialisation parameter; this name is deprecated
	 *   and will be dropped in a future release.
	 */
	private InitParameter(String name, String legacyName) {
		this.name = name;
		this.legacyName = legacyName;
	}
	
	/**
	 * The string representing the name of the initialisation parameter.
	 */
	private String name;
	
	/**
	 * The string prerepsenting the (deprecated) legacy name of the initialisation
	 * parameter.
	 */
	private String legacyName;
	
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
	 * Returns the legacy name of the initialisation parameter.
	 * 
	 * @return
	 *   the legacy name of the initialisation parameter.
	 */
	public String getLegacyName() {
		return legacyName;
	}
	
    /**
     * Retrieves the value of the input parameter for the given portlet, base on 
     * the current name first, and on the legacy name if no valid value could be 
     * found.
     * 
     * @param portlet
     *   the portlet whose parameter's value is to be retrieved.
     * @return
     *   the value of the input parameter.
     */
    public String getValueForPortlet(GenericPortlet portlet) {
    	String value = portlet.getInitParameter(name);
    	if(!Strings.isValid(value)) {
    		logger.warn("using the legacy parameter name '{}', please replace it with '{}' in your portlet.xml", legacyName, name);
    		value = portlet.getInitParameter(legacyName);
    	}
    	return value;
    }	
    
    /**
     * Checks whether the parameter is specified in the portlet.xml in its legacy
     * form.
     *  
     * @param portlet
     *   the portlet whise parameter is to be checked.
     * @return
     *   <code>true</code> if the parameter is specified in its legacy form.
     */
    public boolean isLegacy(GenericPortlet portlet) {
    	return Strings.isValid(legacyName) && Strings.isValid(portlet.getInitParameter(legacyName));
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
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(InitParameter.class);
}
