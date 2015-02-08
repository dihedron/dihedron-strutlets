/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 


package org.dihedron.strutlets.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing the JSR-286 portlet event that an action method supports.
 * By adding this annotation to an action method, it is declared to support the 
 * given event and will be invoked whenever any such event is fired.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface Event {

	/**
	 * Returns the name of the event. 
	 * 
	 * @return
	 *   the name of the event.
	 */
	String value();
	
	/**
	 * Returns the namespace of the event.
	 * 
	 * @return
	 *   the namespace of the event.
	 */
	String namespace() default "";
}