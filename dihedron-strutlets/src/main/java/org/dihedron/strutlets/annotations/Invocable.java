/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dihedron.strutlets.validation.DefaultValidationHandler;
import org.dihedron.strutlets.validation.ValidationHandler;

/**
 * Annotation to be placed on methods that will be exposed as action, event, 
 * render or resource targets.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Invocable {
	
	/**
	 * Indicates the behaviour of the annotated method with respect to the
	 * system's internal state. Idempotent methods are supposed not to change the
	 * state in any way or to be able to handle multiple invocations with the
	 * same parameters in a row, the way it happens when a target is used in render 
	 * URL: the portlet container may invoke the same method, with the same URL
	 * parameters multiple times depending on its need to refresh the page 
	 * contents; non-idempotent targets are not fit to be used in render URLs,
	 * but they have access to additional portal functionalities such as the
	 * ability to set render parameters, to change the portlet state and mode, 
	 * and the capability to fire events.
	 * No enforcement is made about the compliance of the method's behaviour
	 * with what's declared in the annotation; failure to be able to handle 
	 * multiple requests returning consistent results and leaving the system in 
	 * a consistent state may lead to unexpected behaviour and a bad user 
	 * experience.
	 * Indicating a non-idempotent target in a render URL request will result
	 * is an exception being thrown; the association of this check and the default
	 * for this attribute being {@code false} is supposed to help the developer 
	 * spot bugs in her code. 
	 *  
	 * @return
	 *   whether the method may be invoked multiple times in a row and still 
	 *   yield consistent results and leave the system in a consistent state.
	 * 
	 */
	boolean idempotent() default false;
	
	/**
	 * Indicates whether the (non-idempotent) target result should be considered 
	 * cacheable; if cacheable, the framework will store the result and replay it 
	 * when the same submit is repeated multiple times; this effectively bypasses 
	 * the non-idempotent code execution and always presents the user with the 
	 * same result. By default an action is assumed to be non cacheable, and 
	 * effectively is if it happens to manipulates the output stream or is 
	 * non-deterministic (e.g the result depends on the time at which it was 
	 * executed).
	 * @return
	 *   whether the result of the (non-idempotent) action may be considered 
	 *   cacheable and be stored by the framework, e.g. to avoid problems with 
	 *   double form submissions. 
	 */
	boolean cacheable() default false;
			
	/**
	 * The array of portlet events that the annotated action method is declared 
	 * to support.
	 * 
	 * @return
	 *   the array of supported events.
	 */
	Event[] events() default {};		
	
	/**
	 * The array of expected results; each of them will map to the appropriate 
	 * renderer, and will be parameterised according to what is specified in the 
	 * <code>@Result</code> annotation.
	 * 
	 * @return
	 *   the array of expected results.
	 */
	Result[] results() default {};
	
	/**
	 * The optional implementation of the validator interface that will provide
	 * the callbacks to handle JSR349 constraint violations errors on properly
	 * annotated input parameters or on the method itself; if left to the default 
	 * dummy class, validation errors will simply cause a warning message to be 
	 * printed. For the exact contract between validator and validation handler,
	 * see {@link ValidationHandler}.
	 */
	Class<? extends ValidationHandler> validator() default DefaultValidationHandler.class;
}