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

package org.dihedron.strutlets.containers;

import javax.portlet.GenericPortlet;

import org.dihedron.strutlets.InitParameter;
import org.dihedron.strutlets.containers.portlet.PortletContainer;
import org.dihedron.strutlets.containers.portlet.liferay61x.Liferay61xPlugin;
import org.dihedron.strutlets.containers.web.WebContainer;
import org.dihedron.strutlets.containers.web.jbossas7x.JBossAS7xPlugin;
import org.dihedron.strutlets.containers.web.tomcat7x.Tomcat7xPlugin;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ContainerPluginManager {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ContainerPluginManager.class);

	private ContainerPlugin [] webContainers = {
			new Tomcat7xPlugin(),
			new JBossAS7xPlugin()
	};
	
	private ContainerPlugin [] portletContainers = {
			new Liferay61xPlugin()
	};	
	
	/**
	 * Factory method: tries to load an application-server-specific plugin, by
	 * loading the class declared in the portlet.xml (if any) or alternatively 
	 * trying to detect if any of the existing "stock" plugins support the 
	 * current runtime environment.
	 * 
	 * @param portlet
	 *   the current portlet, to get the value of the 
	 *   <code>APPLICATION_SERVER_PLUGIN</code> initialisation parameter.
	 * @return
	 */
	public WebContainer makeWebContainer(GenericPortlet portlet) {
		String classname = InitParameter.WEB_CONTAINER_PLUGIN.getValueForPortlet(portlet);
		if(Strings.isValid(classname)) {			
			try {
				logger.trace("loading application server plugin '{}'...", classname);
				ContainerPlugin plugin = (ContainerPlugin) Class.forName(classname).newInstance();
				logger.trace("application server plugin loaded");
				ContainerProbe probe = plugin.makeContainerProbe();
				if(probe.isAvailable()) {
					logger.trace("probe successfully detected runtime components and settings");
					return (WebContainer)plugin.makeContainer();
				}
			} catch (InstantiationException e) {
				logger.error("error instantiating application server plugin of class '" + classname + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("security restriction apply to class '" + classname + "'", e);
			} catch (ClassNotFoundException e) {
				logger.error("declared application server plugin class '" + classname + "' not found on class path", e);
			}
		} 
		logger.trace("no valid application server plugin declared in portlet.xml, trying to auto-detect");
		for(ContainerPlugin plugin : webContainers) {
			logger.trace("checking if the {} plugin supports the current environment", plugin.getClass().getCanonicalName());			
			ContainerProbe probe = plugin.makeContainerProbe();
			if(probe.isAvailable()) {
				logger.trace("probe successfully detected runtime components and settings");
				return (WebContainer)plugin.makeContainer();
			}
		}
		return null;
	}
	
	/**
	 * Factory method: tries to load an application-server-specific plugin, by
	 * loading the class declared in the portlet.xml (if any) or alternatively 
	 * trying to detect if any of the existing "stock" plugins support the 
	 * current runtime environment.
	 * 
	 * @param portlet
	 *   the current portlet, to get the value of the 
	 *   <code>APPLICATION_SERVER_PLUGIN</code> initialisation parameter.
	 * @return
	 */
	public PortletContainer makePortletContainer(GenericPortlet portlet) {
		String classname = InitParameter.PORTLET_CONTAINER_PLUGIN.getValueForPortlet(portlet);
		if(Strings.isValid(classname)) {			
			try {
				logger.trace("loading portlet container plugin '{}'...", classname);
				ContainerPlugin plugin = (ContainerPlugin) Class.forName(classname).newInstance();
				logger.trace("portlet container plugin loaded");
				ContainerProbe probe = plugin.makeContainerProbe();
				if(probe.isAvailable()) {
					logger.trace("probe successfully detected runtime components and settings");
					return (PortletContainer)plugin.makeContainer();
				}
			} catch (InstantiationException e) {
				logger.error("error instantiating portlet container plugin of class '" + classname + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("security restriction apply to class '" + classname + "'", e);
			} catch (ClassNotFoundException e) {
				logger.error("declared portlet container plugin class '" + classname + "' not found on class path", e);
			}
		} 
		logger.trace("no valid portlet container plugin declared in portlet.xml, trying to auto-detect");
		for(ContainerPlugin plugin : portletContainers) {
			logger.trace("checking if the {} plugin supports the current environment", plugin.getClass().getCanonicalName());			
			ContainerProbe probe = plugin.makeContainerProbe();
			if(probe.isAvailable()) {
				logger.trace("probe successfully detected runtime components and settings");
				return (PortletContainer)plugin.makeContainer();
			}
		}
		return null;
	}	
	
}
