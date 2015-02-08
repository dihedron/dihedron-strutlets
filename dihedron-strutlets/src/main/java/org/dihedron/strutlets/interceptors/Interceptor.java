/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.interceptors;

import java.util.HashMap;
import java.util.Map;

import org.dihedron.strutlets.ActionInvocation;
import org.dihedron.strutlets.exceptions.StrutletsException;

/**
 * @author Andrea Funto'
 */
public abstract class Interceptor {

	/**
	 * The interceptor identifier.
	 */
	private String id;
	
	/**
	 * A map of configuration parameters.
	 */
	private Map<String, String> parameters = new HashMap<String, String>();
		
	/**
	 * Sets the interceptor's unique identifier, namespaced with the stack in 
	 * which it is employed
	 * 
	 * @param stackId 
	 *   the id of the stack in which this interceptor is used.
	 * @param interceptorId
	 *   the interceptor's unique identifier.
	 * @return
	 *   the interceptor itself, for method chaining.
	 */
	public Interceptor setId(String stackId, String interceptorId) {
		this.id = stackId  + "::" + interceptorId;
		return this;
	}

	/**
	 * Returns the interceptor's namespaced unique identifier (with respect to 
	 * the current stack).
	 * 
	 * @return
	 *   the interceptor's namespaced unique identifier.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets an interceptor parameter.
	 * 
	 * @param key
	 *   the parameter name.
	 * @param value
	 *   the parameter value.
	 */
	public void setParameter(String key, String value) {
		if(key != null) {
			parameters.put(key, value);
		}
	}
	
	/**
	 * Retrieves the value for the given parameter key.
	 * 
	 * @param key
	 *   the parameter name.
	 * @return
	 *   the parameter value.
	 */
	public String getParameter(String key) {
		return parameters.get(key);		
	}
	
	/**
	 * Returns the complete set of parameters, as a map.
	 * 
	 * @return
	 *   the complete set of parameters, as a map.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}
	
	/**
	 * Initialises the interceptor; this is the place where any task that should 
	 * be performed before the request processing starts can be accomplished. 
	 * Extending classes may plug in their custom initialisation logic here,
	 * otherwise the default, do-nothing implementation is used.
	 * NOTE: if you need per-user initialisation, this is NOT the place to put it, 
	 * since this method will be called at stack instantiation time, not when 
	 * the user's first request comes in; thus there is no session available when 
	 * this method fires,   
	 */
	public void initialise() {
	}
	
	/**
	 * The method implementing the interceptor's business logic.
	 * 
	 * @param invocation
	 *   the action invocation object.
	 * @return
	 *   a result string.
	 * @throws StrutletsException
	 */
	public abstract String intercept(ActionInvocation invocation) throws StrutletsException;
}
