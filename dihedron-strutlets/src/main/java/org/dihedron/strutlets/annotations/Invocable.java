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