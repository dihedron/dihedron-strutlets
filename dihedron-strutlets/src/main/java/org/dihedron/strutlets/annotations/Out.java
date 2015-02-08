/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
@Target(ElementType.PARAMETER)
public @interface Out {
	
	/**
	 * The name of the output parameter which will receive the annotated field's
	 * value; it must be specified.
	 * 
	 * @return
	 *   the name of the output parameter.
	 */
	String value();

	/**
	 * The scope into which the parameter should be stored; by default, it is
	 * stored among the render parameters.
	 * 
	 * @return
	 *   the scope into which to set the parameter.
	 */
	Scope to() default Scope.REQUEST;	
	
	/**
	 * The scope into which the parameter should be stored; by default, it is
	 * stored among the render parameters.
	 * 
	 * @return
	 *   the scope into which to set the parameter.
	 * @deprecated
	 *   as of release 0.60.0, replaced by {@link #to()}
	 */
	@Deprecated
	Scope scope() default Scope.NONE;	
}