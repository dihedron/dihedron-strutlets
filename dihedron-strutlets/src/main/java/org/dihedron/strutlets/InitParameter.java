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

import org.dihedron.commons.utils.Strings;
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
	 * A properties file used to initialise values for the actions managed by 
	 * this <code>ActionController</code>. The path to the file must be expressed 
	 * as an URL, according to one of the following formats:<ul>
	 * <li>classpath:path/to/resource/on/classpath.properties</li>
	 * <li>http://server:port/path/to/configuration.properties</li>
	 * <li>file://path/to/configuration.properties</li>
	 * </ul>.
	 */
	ACTIONS_CONFIGURATION("strutlets:actions-configuration", ""),
	
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
	INTERCEPTORS_CONFIGURATION("strutlets:interceptors-configuration", "interceptors.configuration.filename"),
	
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
	 * The default page to be shown when an internal error occurs.
	 */
	DEFAULT_ERROR_JSP("strutlets:default-error-page", ""),
	
	/**
	 * The parameter used to specify the root directory for JSP renderers.
	 * This is used only when dealing with annotated actions and smart defaults,
	 * to conjure the name of renderer JSPs based on the action's result and
	 * the current portlet mode.
	 */
	JSP_ROOT_PATH("strutlets:jsp-root-path", "render.root.directory"),
			
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
	JSP_PATH_PATTERN("strutlets:jsp-path-pattern", "render.path.pattern"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in VIEW mode. This page is the starting point of the VIEW mode HTML 
	 * navigation tree.
	 */
	VIEW_MODE_HOME("strutlets:view-home", "render.view.homepage"),
	
	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in EDIT mode. This page is the starting point of the EDIT mode HTML 
	 * navigation tree.
	 */
	EDIT_MODE_HOME("strutlets:edit-home", "render.edit.homepage"),

	/**
	 * The parameter used to specify the home page to be used by the framework
	 * in HELP mode. This page is the starting point of the HELP mode HTML 
	 * navigation tree.
	 */
	HELP_MODE_HOME("strutlets:help-home", "render.help.homepage"),

	/**
	 * The parameter used to specify an optional set of packages to be scanned 
	 * for aapplication-server-specific plugins.
	 */
	WEB_CONTAINER_PACKAGES("strutlets:web-container-packages", ""),	

	/**
	 * The parameter used to specify an optional set of packages to be scanned 
	 * for portlet-container-specific plugins.
	 */
	PORTLET_CONTAINER_PACKAGES("strutlets:portlet-container-packages", ""),
	
	/**
	 * The parameter used to specify an optional application-server-specific plugin,
	 * which will be used by the framework to retrieve platform-specific data.
	 */
	WEB_CONTAINER_PLUGIN("strutlets:web-container-plugin", "web.container.plugin"),
	
	/**
	 * The parameter used to specify an optional portlet-container-specific plugin,
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
    		value = portlet.getInitParameter(legacyName);
    		if(!Strings.isValid(value)) {
    			logger.trace("no value for parameter '{}' (or '{}') in portlet.xml", name, legacyName);
    		} else {
    			logger.warn("using the legacy parameter name '{}', please replace it with '{}' in your portlet.xml", legacyName, name);
    		}
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
