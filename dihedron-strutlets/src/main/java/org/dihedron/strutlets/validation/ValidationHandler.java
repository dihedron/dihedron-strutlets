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
package org.dihedron.strutlets.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.dihedron.strutlets.ActionContext;


/**
 * The base interface to all validation callback handlers; implementors of this
 * class must comply with the following contract:<ol>
 * <li>no "business" exceptions should be thrown</li>
 * <li>if the method returns {@code null}, then the processing will continue as
 * usual</li>
 * <li>if the method returns a string, it will be taken as the result of the 
 * method invocation, without the actual method being invoked; this can be used
 * to divert the ordinary flow of navigation into a path that e.g. shows the user 
 * which values were not correct in the submitted form</li>
 * <li>implementors have access to the {@link ActionContext}, so they can store 
 * values in any admitted scope before routing to the appropriate renderer</li>
 * </ol>. 
 * 
 * @author Andrea Funto'
 */
public interface ValidationHandler {
	
	/**
	 * This method is invoked when the JSR-349 validator produces at least one 
	 * constraint violation on the parameters, right before the action's execution.
	 * Based on the return value, it can prevent the action from being executed 
	 * at all.
	 * 
	 * @param violations
	 *   the set of violations.
	 * @return
	 *   @{code null} to let the processing proceed as usual; a valid string to
	 *   divert execution flow into a different path (this value will be assumed 
	 *   to be the action's result, without executing it). 
	 */
	String onParametersViolations(Set<ConstraintViolation<?>> violations);
	
	/**
	 * This method is invoked when the JSR-349 validator produces at least one 
	 * constraint violation on the action method's return value. It is called
	 * rigth after th actual action has executed, so it cannot be used to prevent
	 * execution, but it can route output rendering to a different path if 
	 * necessary.
	 * 
	 * @param violations
	 *   the set of violations.
	 * @return
	 *   @{code null} to let the processing proceed as usual; a valid string to
	 *   divert execution flow into a different path (this value will replace
	 *   the action's result). 
	 */
	String onResultViolations(Set<ConstraintViolation<?>> violations);

}
