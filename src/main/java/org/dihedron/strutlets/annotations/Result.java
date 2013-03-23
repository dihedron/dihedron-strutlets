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
 * Annotation representing the view handler that will 
 * create the visual representation for the given result.
 * Results can be represented as free text strings, and
 * are mapped to the appropriate view handler by the action
 * controller, based on what's in these annotations.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface Result {

	/**
	 * The result string for which the mapping to the renderer is 
	 * being described.
	 * 
	 * @return
	 *   the result string.
	 */
	String value() default "success";
	
	/**
	 * The class of the view handler, which will format the output
	 * based on the annotated action's outcome and the parameters,
	 * as a class name; this property can be used when the renderer
	 * is loaded lazily, and only bound at runtime, e.g. because it is 
	 * in a plug-in and must be located by name; if it is known at build
	 * time, use the <code>classref</code> property instead.
	 * 
	 * @return
	 *   the name of the renderer class.
	 */
	String mode() default "same";
	
	/**
	 * The class of the view handler, which will format the output
	 * based on the annotated action's outcome and the parameters,
	 * as a class object; this propery can be used when the renderer
	 * is known at build time and eagerly bound; if it is not known 
	 * at build time, e.g. because it is in a plug-in that is only 
	 * loaded at runtime, use the <code>classname</code> property 
	 * instead.
	 * 
	 * @return
	 *   the renderer class.
	 */
	String state() default "same";	
	
	/**
	 * Constants to be passed.
	 * 
	 * @return
	 *   the parameters to be passed to the renderer 
	 *   at instantiation time.
	 */
	String url() default "";

}