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

package org.dihedron.el.operators;


/**
 * Base interface for all object graph navigation operators.
 * 
 * @author Andrea Funto'
 */
public interface Operator {
	
	/**
	 * Initialises the operator.
	 * 
	 * @param argument
	 *   the operator initialisation argument.
	 * @return
	 *   the operator itself, for chaining.
	 */
	Operator initialise(String argument);
	
	/**
	 * Applies the operator onto the given object, returning the
	 * result of the elaboration.
	 * 
	 * @param operand
	 *   the operand.
	 * @return
	 *   the result, which might on its turn become the next operator's
	 *   operand.
	 * @throws Exception
	 *   if any error occurs during the processing.
	 */
	Object apply(Object operand) throws Exception;
}
