/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.annotations;

/**
 * Elements in this enumeration express the scope in which a parameter should be 
 * looked up during input injection and into which an output should be stored
 * at output extraction, after the action has been invoked.
 *  
 * @author Andrea Funto'
 */
public enum Scope {
	
	/**
	 * The parameter is to be looked for in the submitted form; this value is not 
	 * acceptable for output storage.
	 */
	FORM,
	
	/**
	 * The parameter is to be stored among the render parameters.
	 */
	RENDER,
	
	/**
	 * The parameter is to be looked up or stored in the current request scope.
	 * 
	 * @see org.dihedron.strutlets.ActionContextImpl.Scope.REQUEST.
	 */
	REQUEST,

	/**
	 * The parameter is to be looked up or stored in the current session scope.
	 * 
	 * @see org.dihedron.strutlets.ActionContextImpl.Scope.PORTLET.
	 */
	PORTLET,
	
	/**
	 * The parameter is to be looked up or stored in the current application scope.
	 * 
	 * @see org.dihedron.strutlets.ActionContextImpl.Scope.APPLICATION.
	 */
	APPLICATION,
	
	/**
	 * The parameter is to be looked up in the current actions' configuration,
	 * if the action is configured via an XML; updating the configuration via
	 * storage is supported, but deprecated as it can lead to situations where
	 * debugging is extremely difficult.
	 */
	CONFIGURATION,
	
	/**
	 * The parameter can be found among the HTTP request parameters, in a portlet-
	 * container specific way.
	 */
	HTTP,
	
	/**
	 * An invalid scope to state that no valid value has been chosen. 
	 */
	NONE
}