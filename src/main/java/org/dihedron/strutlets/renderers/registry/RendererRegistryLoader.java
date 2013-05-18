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

package org.dihedron.strutlets.renderers.registry;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.actions.registry.ActionRegistry;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.utils.Strings;
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
public class RendererRegistryLoader {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RendererRegistryLoader.class);
	
	/**
     * This method performs the automatic scanning of dynamic actions at startup
     * time, to make access to actions faster later on. The targets map is
     * pre-populated with information coming from actions configured through
     * annotations and residing in the default java package, as per the user's 
     * configuration.
     * 
     * @throws StrutletsException
     */
    public void loadFromJavaPackage(ActionRegistry configuration, String javaPackage) throws StrutletsException {
    	
    	if(Strings.isValid(javaPackage)) {
    		logger.trace("looking for action classes in package '{}'", javaPackage);

    		// use this approach because it seems to be consistently faster
    		// than the much simpler new Reflections(javaPackage) 
    		Reflections reflections = 
    				new Reflections(new ConfigurationBuilder()
    					.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(javaPackage)))
    					.setUrls(ClasspathHelper.forPackage(javaPackage))
    					.setScanners(new SubTypesScanner()));    		
    		Set<Class<? extends Action>> actions = reflections.getSubTypesOf(Action.class);
	        for(Class<?> clazz : actions) {
	        	logger.trace("action class: '{}'", clazz.getName());
	        	Class<?> iteratorClass = clazz;
	        	Set<Method> methods = new HashSet<Method>();
	        	while(iteratorClass != null && iteratorClass!= Object.class) { 
	        		Method[] set = iteratorClass.getDeclaredMethods();
	        		methods.addAll(Arrays.asList(set));
	        		iteratorClass = iteratorClass.getSuperclass();
	        	}
	        	for(Method method : methods) {	        		
	        		if(method.isAnnotationPresent(Invocable.class)) {
		        		logger.trace("checking annotated method '{}' in class '{}'", method.getName(), clazz.getSimpleName());
		        		configuration.addTarget(clazz.getSimpleName(), method.getName());
	        		} else {
	        			logger.trace("discarding unannotated method '{}' in class '{}'", method.getName(), clazz.getSimpleName());
	        		}
	        	}
	        }
    	}
    }		
}
