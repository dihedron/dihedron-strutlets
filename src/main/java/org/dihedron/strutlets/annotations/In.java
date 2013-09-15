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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the field will contain data coming from the given 
 * parameter name.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target( ElementType.PARAMETER )
public @interface In {
	
	/**
	 * The name of the input parameter; it must be specified.
	 * 
	 * @return
	 *   the name of the parameter, or the empty string if the name matches that 
	 *   of the field.
	 */
	String value();
	
	/**
	 * The scope in which the parameter should be looked up; by default, it is
	 * looked up in all available scopes.
	 * 
	 * @return
	 *   the scope of the parameter.
	 */
	Scope[] scopes() default { Scope.FORM, Scope.REQUEST, Scope.PORTLET, Scope.APPLICATION, Scope.CONFIGURATION };
}