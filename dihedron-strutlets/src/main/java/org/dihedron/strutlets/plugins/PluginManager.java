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

package org.dihedron.strutlets.plugins;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class PluginManager {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);

	
	/**
	 * Returns all the plugins available in the class path.
	 * 
	 * @return
	 *   all the plugins available in the class path.
	 */
	public static Set<Class<? extends Plugin>> getPlugins() {
		Reflections reflections = 
				new Reflections(new ConfigurationBuilder()
					.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("")))
					.setUrls(ClasspathHelper.forPackage("org.dihedron"))
					.setScanners(new SubTypesScanner())
				);
		Set<Class<? extends Plugin>> plugins = reflections.getSubTypesOf(Plugin.class);
		logger.trace("found {} plugins in classpath", plugins.size());
		return plugins;
	}
	
	/**
	 * Returns all the plugins of the given type available on the class path.
	 * 
	 * @param filter
	 *   the plugin class.
	 * @return
	 *   all the plugins of the given type available on the class path.
	 */
	public static Set<Class<? extends Plugin>> getPlugins(Class<? extends Plugin> filter) {
		Set<Class<? extends Plugin>> plugins = new HashSet<Class<? extends Plugin>>();
		for(Class<? extends Plugin> clazz : getPlugins()) {
			if(Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
				logger.trace("skipping class '{}' as it is abstract (or an interface)", clazz.getCanonicalName());
				continue;
			}
			if(filter.isAssignableFrom(clazz)) {
				logger.trace("plugin '{}' is of type '{}'", clazz.getCanonicalName(), filter.getCanonicalName());
				plugins.add(clazz);
			} else {
				logger.trace("plugin '{}' is not of type '{}'", clazz.getCanonicalName(), filter.getCanonicalName());
			}
		}
		return plugins;
	}
	
	/**
	 * Loads the actual business logic implementations of the <code>Pluggable</code>
	 * class from the given set of plugins.
	 * 
	 * @param plugins
	 *   the set of plugins that must be asked for support on the current environemnt.
	 * @return
	 *   a set of actual business logic <code>Pluggable</code> objects..
	 */
	public static List<Pluggable> loadPluggable(Set<Class<? extends Plugin>> plugins) {
		List<Pluggable> pluggables = new ArrayList<Pluggable>();
		for(Class<? extends Plugin> clazz : plugins) {
			try {
				Plugin plugin = (Plugin)clazz.newInstance();
				Probe probe = plugin.makeProbe();
				if(probe.isSupportedEnvironment()) {
					logger.trace("plugin '{}' supports the current environemnt", clazz.getCanonicalName());
					pluggables.add(plugin.makePluggable());
				} else {
					logger.trace("plugin '{}' does not support the current environemnt", clazz.getCanonicalName());
				}
			} catch (InstantiationException e) {
				logger.error("error instantiating plugin of class '" + clazz.getCanonicalName() + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("illegal access to plugin of class '" + clazz.getCanonicalName() + "'", e);
			}
		}
		return pluggables;
	}
	
	/**
	 * Returns the first <code>Pluggable</code> object from the given set of plugins.
	 * 
	 * @param plugins
	 *   the set of plugins.
	 * @return
	 *   the first <code>Pluggable</code> object from the set of supporting
	 *   plugins, null if none found. 
	 */
	public static Pluggable loadFirstPluggable(Set<Class<? extends Plugin>> plugins) {
		List<Pluggable> pluggables = loadPluggable(plugins);
		if(!pluggables.isEmpty()) {
			return pluggables.get(0);
		}
		return null;
	}
	
//	/**
//	 * Factory method: tries to load an application-server-specific plugin, by
//	 * loading the class declared in the portlet.xml (if any) or alternatively 
//	 * trying to detect if any of the existing "stock" plugins support the 
//	 * current runtime environment.
//	 * 
//	 * @param portlet
//	 *   the current portlet, to get the value of the 
//	 *   <code>APPLICATION_SERVER_PLUGIN</code> initialisation parameter.
//	 * @return
//	 */
//	public WebContainer makeWebContainer(GenericPortlet portlet) {
//		String classname = InitParameter.WEB_CONTAINER_PLUGIN.getValueForPortlet(portlet);
//		
//		Reflections reflections = 
//				new Reflections(new ConfigurationBuilder()
//					.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("")))
//						.setUrls(ClasspathHelper.forPackage(""))
//						.setScanners(new SubTypesScanner())
//					);
//		Set<Class<? extends WebContainer>> plugins = reflections.getSubTypesOf(WebContainer.class);
//        for(Class<?> clazz : plugins) {
//        	logger.trace("loading web container plugin '{}'...", clazz.getCanonicalName());
//        	ContainerPlugin plugin = (ContainerPlugin)clazz.newInstance();
//        	
//			try {
//				logger.trace("loading web container plugin '{}'...", clazz.getCanonicalName());
//				ContainerPlugin plugin = (ContainerPlugin) Class.forName(classname).newInstance();
//				logger.trace("application server plugin loaded");
//				Probe probe = plugin.makeContainerProbe();
//				if(probe.isAvailable()) {
//					logger.trace("probe successfully detected runtime components and settings");
//					return (WebContainer)plugin.makeContainer();
//				}
//			} catch (InstantiationException e) {
//				logger.error("error instantiating application server plugin of class '" + classname + "'", e);
//			} catch (IllegalAccessException e) {
//				logger.error("security restriction apply to class '" + classname + "'", e);
//			} catch (ClassNotFoundException e) {
//				logger.error("declared application server plugin class '" + classname + "' not found on class path", e);
//			}
//        }
//		
//		logger.trace("no valid application server plugin declared in portlet.xml, trying to auto-detect");
//		for(ContainerPlugin plugin : webContainers) {
//			logger.trace("checking if the {} plugin supports the current environment", plugin.getClass().getCanonicalName());			
//			Probe probe = plugin.makeContainerProbe();
//			if(probe.isAvailable()) {
//				logger.trace("probe successfully detected runtime components and settings");
//				return (WebContainer)plugin.makeContainer();
//			}
//		}
//		return null;
//	}
//	
//	/**
//	 * Factory method: tries to load an application-server-specific plugin, by
//	 * loading the class declared in the portlet.xml (if any) or alternatively 
//	 * trying to detect if any of the existing "stock" plugins support the 
//	 * current runtime environment.
//	 * 
//	 * @param portlet
//	 *   the current portlet, to get the value of the 
//	 *   <code>APPLICATION_SERVER_PLUGIN</code> initialisation parameter.
//	 * @return
//	 */
//	public PortletContainer makePortletContainer(GenericPortlet portlet) {
//		String classname = InitParameter.PORTLET_CONTAINER_PLUGIN.getValueForPortlet(portlet);
//		if(Strings.isValid(classname)) {			
//			try {
//				logger.trace("loading portlet container plugin '{}'...", classname);
//				ContainerPlugin plugin = (ContainerPlugin) Class.forName(classname).newInstance();
//				logger.trace("portlet container plugin loaded");
//				Probe probe = plugin.makeContainerProbe();
//				if(probe.isAvailable()) {
//					logger.trace("probe successfully detected runtime components and settings");
//					return (PortletContainer)plugin.makeContainer();
//				}
//			} catch (InstantiationException e) {
//				logger.error("error instantiating portlet container plugin of class '" + classname + "'", e);
//			} catch (IllegalAccessException e) {
//				logger.error("security restriction apply to class '" + classname + "'", e);
//			} catch (ClassNotFoundException e) {
//				logger.error("declared portlet container plugin class '" + classname + "' not found on class path", e);
//			}
//		} 
//		logger.trace("no valid portlet container plugin declared in portlet.xml, trying to auto-detect");
//		for(ContainerPlugin plugin : portletContainers) {
//			logger.trace("checking if the {} plugin supports the current environment", plugin.getClass().getCanonicalName());			
//			Probe probe = plugin.makeContainerProbe();
//			if(probe.isAvailable()) {
//				logger.trace("probe successfully detected runtime components and settings");
//				return (PortletContainer)plugin.makeContainer();
//			}
//		}
//		return null;
//	}	
	
}
