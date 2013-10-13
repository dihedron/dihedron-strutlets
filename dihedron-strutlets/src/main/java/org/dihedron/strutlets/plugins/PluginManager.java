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
	 * Loads a plug-in given its factory class; in order to do so, it loads the 
	 * probe and checks if the plug-in is supported on the current environment.
	 * 
	 * @param clazz
	 *   the class for the plug-in factory interface.
	 * @return
	 *   the <code>Plugin</code> if it could be loaded, null otherwise.
	 */
	public static Plugin loadPlugin(Class<? extends PluginFactory> clazz) {
		Plugin plugin = null;
		try {
			logger.trace("attempting to load plugin from '{}' factory class", clazz.getCanonicalName());
			PluginFactory factory = clazz.newInstance();
			logger.trace("factory class '{}' loaded, checking if supported on current platform", clazz.getCanonicalName());
			Probe probe = factory.makeProbe();
			if(probe.isSupportedEnvironment()) {
				logger.trace("probe returned success, loading plugin class");
				plugin = factory.makePlugin();
				if(plugin != null) {
					logger.trace("plugin of class '{}' loaded", plugin.getClass().getCanonicalName());
				}
			}
		} catch (InstantiationException e) {
			logger.error("error instantiating class '" + clazz.getCanonicalName() + "', skipped", e);
		} catch (IllegalAccessException e) {
			logger.error("illegal access to class class '" + clazz.getCanonicalName() + "', skipped", e);
		}
		return plugin;
	}
	
	/**
	 * Loads a plug-in given the name of its factory class; in order to do so, it
	 * loads the probe and checks if the plug-in is supported on the current 
	 * environment.
	 * 
	 * @param factoryClass
	 *   the name of the class for the plug-in factory interface.
	 * @return
	 *   the <code>Plugin</code> if it could be loaded, null otherwise.
	 */
	public static Plugin loadPlugin(String factoryClass) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends PluginFactory> factory = (Class<? extends PluginFactory>) Class.forName(factoryClass);
			return loadPlugin(factory);
		} catch (ClassNotFoundException e) {
			logger.error("error loading class '" + factoryClass + "'", e);
		}
		return null;
	}
	
	/**
	 * Loads all the available and supporting <code>Plugin</code> found under the 
	 * given set of paths.
	 * 
	 * @param type
	 *   the type of <code>PluginFactory</code> we're looking for.
	 * @param paths
	 *   a set of paths on the class path.
	 * @return
	 */
	public static List<Plugin> loadPluginsInPath(Class<? extends PluginFactory> type, String... paths) {
		List<Plugin> plugins = new ArrayList<Plugin>();
		List<PluginFactory> factories = findPluginFactoriesByType(type, paths);
		for(PluginFactory factory : factories) {
			Probe probe = factory.makeProbe();
			if(probe.isSupportedEnvironment()) {
				try {
					Plugin plugin = factory.makePlugin();
					if(plugin != null) {
						logger.trace("plugin of class '{}' loaded", plugin.getClass().getCanonicalName());
						plugins.add(plugin);
					}
				} catch(Exception e) {
					logger.error("error instantiating plugin for " + factory.getClass().getSimpleName(), e);
				}
			}			
		}
		return plugins;
	}
	
	/**
	 * Finds, loads and returns all the <code>PluginFactory</code> instances of
	 * available plug-in of the give type (or subtypes thereof) on the class path.
	 * 
	 * @param type
	 *   the type of plug-in factory we're looking for.
	 * @param paths
	 *   a set of path on the classpath to scan for plug-in factory.
	 * @return
	 *   a list of instantiated <code>PluginFactory</code>s.
	 */
	private static List<PluginFactory> findPluginFactoriesByType(Class<? extends PluginFactory> type, String... paths) {
		List<PluginFactory> factories = new ArrayList<PluginFactory>();
		for(Class<? extends PluginFactory> clazz : findPluginFactories(paths)) {
			if(Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
				logger.trace("skipping plugin factory class '{}' as it is abstract (or an interface)", clazz.getCanonicalName());
				continue;
			}
			if(type.isAssignableFrom(clazz)) {
				logger.trace("plugin factory '{}' is of type '{}'", clazz.getCanonicalName(), type.getCanonicalName());
				try {
					factories.add((PluginFactory)clazz.newInstance());
					logger.trace("plugin factory of class '{}' instantiated", clazz.getCanonicalName());
				} catch (InstantiationException e) {
					logger.error("error instantiating class '" + clazz.getCanonicalName() + "', skipped", e);
				} catch (IllegalAccessException e) {
					logger.error("illegal access to class class '" + clazz.getCanonicalName() + "', skipped", e);
				}
			} else {
				logger.trace("plugin factory '{}' is not of type '{}'", clazz.getCanonicalName(), type.getCanonicalName());
			}
		}
		return factories;		
	}
	
	/**
	 * Returns all the plug-in factories available in the given set of class paths.
	 * 
	 * @return
	 *   all the plug-in factories available in the given set of class paths.
	 */
	private static Set<Class<? extends PluginFactory>> findPluginFactories(String... paths) {
		Set<Class<? extends PluginFactory>> classes = new HashSet<Class<? extends PluginFactory>>();
		for(String path : paths) {
			logger.trace("looking up plugin factory under '{}'...", path);		
			Reflections reflections = 
					new Reflections(new ConfigurationBuilder()
						.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("")))
						.setUrls(ClasspathHelper.forPackage(path))
						.setScanners(new SubTypesScanner())
					);
			Set<Class<? extends PluginFactory>> found = reflections.getSubTypesOf(PluginFactory.class);			
			logger.trace("... found {} plugin factory under '{}'", found.size(), path);
			classes.addAll(found);
		}
		logger.trace("found {} plugin factory under given paths", classes.size());
		return classes;
	}
}
