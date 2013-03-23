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

import org.dihedron.strutlets.actions.Semantics;

/**
 * Annotation representing the list of outcomes for the given
 * action; each outcome will describe the renderer to be used 
 * for the given result.
 * 
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
public @interface Invocable {
	
	/**
	 * Indicates the behaviour of the annotated method with respect to the
	 * system's internal state. READ_ONLY methods are supposed not to change the
	 * state in any way and are fit to be used in render requests; READ_WRITE 
	 * methods, on the contrary, may change the system's internal state and can 
	 * only be used in the context of action or event processing.
	 * No enforcement is made about the compliance of the method's behaviour
	 * with what's declared in the annotation; failure to keep status unchanged 
	 * in READ_ONLY methods can lead to unpredictable behaviour in the application.
	 * indicating a READ_WRITE method in a render request will result in an
	 * exception being thrown. 
	 *  
	 * @return
	 *   whether the method may change the system's status (READ_WRITE, the 
	 *   default) or will leave it untouched (READ_ONLY). 
	 * 
	 */
	Semantics semantics() default Semantics.READ_WRITE;
	
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
	 * The array of portlet events that an action method is declared to support.
	 * 
	 * @return
	 *   the array of supported events.
	 */
	Event[] events() default {};	
}