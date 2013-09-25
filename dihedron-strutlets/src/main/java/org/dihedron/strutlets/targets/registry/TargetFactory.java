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
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import org.dihedron.commons.utils.Strings;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.aop.ActionProxy;
import org.dihedron.strutlets.aop.ActionProxyFactory;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
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
	 * The object that takes care of inspecting the action and creating proxy 
	 * class, static factory method and method proxies for its <code>@Invocable
	 * </code> methods.
	 */
	private ActionProxyFactory factory = new ActionProxyFactory();
	
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
    					//.setScanners(new SubTypesScanner()));
    					.setScanners(new TypeAnnotationsScanner()));
//    		Set<Class<? extends AbstractAction>> actionClasses = reflections.getSubTypesOf(AbstractAction.class);
    		Set<Class<?>> actions = reflections.getTypesAnnotatedWith(Action.class);
	        for(Class<?> action : actions) {
	        	makeFromJavaClass(registry, action);
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
    public void makeFromJavaClass(TargetRegistry registry, Class<?> actionClass) throws StrutletsException {
    	logger.trace("analysing action class: '{}'...", actionClass.getName());

    	// only add classes that are not abstract to the target registry
    	if(!Modifier.isAbstract(actionClass.getModifiers())) {
    		logger.trace("class '{}' is not abstract", actionClass.getSimpleName());
   
	    	String interceptors = actionClass.getAnnotation(Action.class).interceptors(); 
	    	
	    	// let the factory inspect the action and generate a factory method
	    	// ans a set of proxy methods for valid @Invocable-annotated action methods 
	    	// (possibly walking up the class hierarchy and discarding duplicates, 
	    	// static and unannotated methods...) 
	    	ActionProxy proxy = factory.makeActionProxy(actionClass);
	    	
	    	// now loop through annotated methods and add them to the registry as targets
	    	Map<Method, Method> methods = proxy.getMethods();
	    	for(Method actionMethod : methods.keySet()) {	        		
	    		if(actionMethod.isAnnotationPresent(Invocable.class)) {
	    			Method proxyMethod = methods.get(actionMethod);
	        		logger.trace("... adding annotated method '{}' in class '{}' (proxy: '{}' in class '{}')", actionMethod.getName(), 
	        				actionClass.getSimpleName(), proxyMethod.getName(), proxy.getProxyClass().getSimpleName());
	        		Invocable invocable = actionMethod.getAnnotation(Invocable.class); 
	        		registry.addTarget(actionClass, proxy.getFactoryMethod(), actionMethod, proxyMethod, invocable, interceptors);
	    		} else {
	    			logger.trace("... discarding unannotated method '{}' in class '{}'", actionMethod.getName(), actionClass.getSimpleName());
	    		}
	    	}
    	} else {
			// if the input class is abstract, we skip it altogether: its methods 
			// will be made available through its subclasses (if ever)    		
    		logger.info("discarding abstract class '{}'", actionClass.getSimpleName());
    	}
    	logger.trace("... done analysing action class: '{}'!", actionClass.getName());
    }
}
