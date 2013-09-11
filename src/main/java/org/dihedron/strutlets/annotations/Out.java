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
 * Annotation indicating that the field will contain 
 * output data, mapped to the given parameter name.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Out {
	
	/**
	 * The name of the output parameter which will receive the annotated field's
	 * value; if not specified, a parameter with the same name as the fields will
	 * be set.
	 * 
	 * @return
	 *   the name of the output parameter.
	 */
	String value() default "";
	
	/**
	 * The scope into which the parameter should be stored; by default, it is
	 * stored among the render parameters.
	 * 
	 * @return
	 *   the scope into which to set the parameter.
	 */
	Scope scope() default Scope.RENDER;	
}