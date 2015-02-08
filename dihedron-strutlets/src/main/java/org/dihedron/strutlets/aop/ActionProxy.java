/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Funto'
 */
public class ActionProxy {

	/**
	 * The dynamically-generated and classloader-injected proxy class, exposing 
	 * one static method for each invocable action method, plus a factory method
	 * to create instances of the action class without resorting to reflection. 
	 */
	private Class<?> proxyClass;
	
	/**
	 * A factory method that implements action allocation.
	 */
	private Method factory;
	
	/**
	 * A map of original action methods to static proxy methods.
	 */
	private Map<Method, Method> methods = new HashMap<Method, Method>();	
	
	/**
	 * Constructor with package visibility, so it cannot be instantiated outside
	 * the current package.
	 */
	ActionProxy() {		
	}
	
	/**
	 * Returns the <code>Class</code> object representing the class.
	 * 
	 * @return
	 *   the <code>Class</code> object representing the class.
	 */
	public Class<?> getProxyClass() {
		return proxyClass;
	}

	/**
	 * Sets the <code>Class</code> object representing the class.
	 * 
	 * @param proxyClass
	 *   the <code>Class</code> object representing the class.
	 */
	void setProxyClass(Class<?> proxyClass) {
		this.proxyClass = proxyClass;
	}

	/**
	 * Returns the factory method that will instantiate a new <code>AbstractAction</code>
	 * instance without resorting to reflection.
	 * 
	 * @return
	 *   the AbstractAction's factory method.
	 */
	public Method getFactoryMethod() {
		return factory;
	}

	/**
	 * Sets the factory method that will instantiate a new <code>AbstractAction</code>
	 * instance without resorting to reflection.
	 * 
	 * @param factory
	 *   the AbstractAction's factory method.
	 */
	void setFactoryMethod(Method factory) {
		this.factory = factory;
	}

	/**
	 * Returns the methods map, providing a proxy method for each invocable method 
	 * in the original action class.
	 * 
	 * @return
	 *   the methods map.
	 */
	public Map<Method, Method> getMethods() {
		return methods;
	}

	/**
	 * Sets the methods map, providing a proxy method for each invocable method 
	 * in the original action class.
	 * 
	 * @param methods
	 *   the methods map.
	 */
	void setMethods(Map<Method, Method> methods) {
		this.methods = methods;
	}
}
