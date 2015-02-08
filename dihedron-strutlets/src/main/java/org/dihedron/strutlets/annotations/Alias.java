/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating the alias for the type onto which it is imposed.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.TYPE)
public @interface Alias {
	
	/**
	 * The alias for the type; by annotating a class with this information you 
	 * provide an alternative name for the type.
	 * 
	 * @return
	 *   the alias for the type.
	 */
	String value();
}