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
	CONFIGURATION;
	
//	/**
//	 * The parameter can be found among the HTTP request parameters, in a portlet-
//	 * container specific way.
//	 */
//	HTTP
}