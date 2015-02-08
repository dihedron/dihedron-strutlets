/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
	 *   the name of the parameter; this must not be a null or blank string since 
	 *   there's no way to acquire a sensible default from the information available
	 *   at runtime (e.g. there's no name of the field available through reflection).
	 */
	String value();

	/**
	 * The scope in which the parameter should be looked up; by default, it is
	 * looked up in all available scopes.
	 * 
	 * @return
	 *   the scope of the parameter.
	 */
	Scope[] from() default { Scope.FORM, Scope.REQUEST, Scope.PORTLET, Scope.APPLICATION, /*Scope.HTTP,*/Scope.CONFIGURATION };
	
	/**
	 * The scope in which the parameter should be looked up; by default, it is
	 * looked up in all available scopes.
	 * 
	 * @return
	 *   the scope of the parameter.
	 * @deprecated
	 *   as of release 0.60.0, replaced by {@link #from()}
	 */
	@Deprecated
	Scope[] scopes() default { };
}