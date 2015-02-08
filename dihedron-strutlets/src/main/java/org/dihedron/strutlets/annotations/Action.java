/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to annotate <code>AbstractAction</code> classes in order to specify
 * the id of the interceptor's stack to be used with it; by default, the framework
 * assumes that unless specified all actions go throught the "default" interceptor
 * stack, which is also the default of the annotation value.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
public @interface Action {

	/**
	 * The name of the default stack.
	 */
	public static final String DEFAULT_INTERCEPTORS_STACK = "default";	
	
	/**
	 * The default return code for a successful execution.
	 */
	public static final String SUCCESS = "success";
	
	/**
	 * The default return code to request more input data.
	 */
	public static final String INPUT = "input";
	
	/**
	 * The default return code for a failed execution.
	 */
	public static final String ERROR = "error";
	
	/**
	 * An alternativa name for the action, so that the user need not know the 
	 * name of the concrete class implementing the business logic.
	 * 
	 * @return
	 *   the alias under which the developer wants the <code>Action</code> to be
	 *   exposed to users; if left to the default, the <code>Action</code>'s name
	 *   will be the name of the class.
	 */
	String alias() default "";
	
	/**
	 * The id of the interceptors' stack to be used with this action.
	 * 
	 * @return
	 *   the id if the interceptors' stack.
	 */
	String interceptors() default DEFAULT_INTERCEPTORS_STACK;
}