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
 * set of parameters, whose name is expressed as regular expression.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target( ElementType.PARAMETER )
public @interface Model {
	
	/**
	 * The pattern (regular expression) of the names of the input parameters; it 
	 * must be specified.
	 * 
	 * @return
	 *   the pattern of the regular expression used to select the parameters
	 *   whose values will be stored inside the field.
	 */
	String value();
	
	/**
	 * A regular expression used to identify the part of the name of the parameter 
	 * that must be dropped in order to get the OGNL expression of the destination
	 * field for each value in the matching input parameters: for instance, if the 
	 * parameters are named "user:name", "user:surname" and "user:address.street", 
	 * the default mask will remove the "user:" prefix, and "name", "surname" 
	 * and "address.street" will be considered OGNL expressions, which will result
	 * in <code>setName()</code>, <code>setSurname()</code> and <code>getAddress().
	 * setStreet()</code> being called on the field.   
	 * 
	 * @return
	 *   a regular expression identifying a set of characters to remove from the
	 *   name in order to get a valid and applicable OGNL expression.
	 */
	String mask() default "^[a-zA-Z0-9_\\-]*\\:";
	
	/**
	 * The scope in which the parameters should be looked up; by default, they
	 * are looked up in all available scopes.
	 * 
	 * @return
	 *   the scope of the parameter.
	 */
	Scope[] from() default { Scope.FORM, Scope.REQUEST, Scope.PORTLET, Scope.APPLICATION, /*Scope.HTTP,*/Scope.CONFIGURATION };
}