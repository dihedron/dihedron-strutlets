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

package org.dihedron.strutlets.targets.registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.Interceptors;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.aop.ActionInstrumentor;
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
 * The class responsible for loading the actions configuration ("targets"), either
 * from the XML file or from the classpath.
 * 
 * @author Andrea Funto'
 */
public class TargetFactory {

	/**
	 * The logger.
	 */
	private static Logger logger = LoggerFactory.getLogger(TargetFactory.class);
	
	/**
	 * The object that takes care of inspecting the action and creating class and 
	 * method proxies for its <code>@Invocable</code> methods.
	 */
	private ActionInstrumentor instrumentor = new ActionInstrumentor();
	
    /**
     * This method performs the automatic scanning of actions at startup time, 
     * to make access to actions faster later on. The targets map is pre-populated 
     * with information coming from actions configured through annotations.
     * 
     * @param registry
     *   the repository where new targets will be stored.
     * @param javaPackage
     *   the Java package to be scanned for actions. 
     * @throws StrutletsException
     */
    public void makeFromJavaPackage(TargetRegistry registry, String javaPackage) throws StrutletsException {
    	
    	if(Strings.isValid(javaPackage)) {
    		logger.trace("looking for action classes in package '{}'", javaPackage);

    		// use this approach because it seems to be consistently faster
    		// than the much simpler new Reflections(javaPackage) 
    		Reflections reflections = 
    				new Reflections(new ConfigurationBuilder()
    					.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(javaPackage)))
    					.setUrls(ClasspathHelper.forPackage(javaPackage))
    					.setScanners(new SubTypesScanner()));    		
    		Set<Class<? extends Action>> actionClasses = reflections.getSubTypesOf(Action.class);
	        for(Class<? extends Action> actionClass : actionClasses) {
	        	makeFromJavaClass(registry, actionClass);
	        }
    	}
    }
    
    /**
     * Scans the given class for annotated methods and adds them to the registry
     * as targets.
     * 
     * @param registry
     *   the repository where new targets will be stored.
     * @param actionClass
     *   the action class to be scanned for annotated methods (targets).
     * @throws StrutletsException 
     */
    public void makeFromJavaClass(TargetRegistry registry, Class<? extends Action> actionClass) throws StrutletsException {
    	logger.trace("analysing action class: '{}'...", actionClass.getName());
    	
    	String interceptors = "default";
    	
    	if(actionClass.isAnnotationPresent(Interceptors.class)) {
    		interceptors = actionClass.getAnnotation(Interceptors.class).value();
    	}
    	
    	// let the instrumentor inspect the action and generate proxy methods for
    	// valid @Invocable-annotated action methods (possibly walking up the
    	// class hierarchy and discarding duplicates, static and unannotated 
    	// methods...) 
    	Map<Method, Method> methods = new HashMap<Method, Method>();
    	Class<?> proxyClass = instrumentor.instrument(actionClass, methods);
    	
    	// now loop through annotated methods and add them to the registry as targets 
    	for(Method actionMethod : methods.keySet()) {	        		
    		if(actionMethod.isAnnotationPresent(Invocable.class)) {
    			Method proxyMethod = methods.get(actionMethod);
        		logger.trace("... adding annotated method '{}' in class '{}' (proxy: '{}' in class '{}')", actionMethod.getName(), 
        				actionClass.getSimpleName(), proxyMethod.getName(), proxyClass.getSimpleName());
        		Invocable invocable = actionMethod.getAnnotation(Invocable.class); 
        		registry.addTarget(actionClass, actionMethod, proxyMethod, invocable, interceptors);
    		} else {
    			logger.trace("... discarding unannotated method '{}' in class '{}'", actionMethod.getName(), actionClass.getSimpleName());
    		}
    	}
    	logger.trace("... done analysing action class: '{}'!", actionClass.getName());
    }
}
