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

import org.dihedron.reflection.Reflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performa a method invocation on the operand.
 * 
 * @author Andrea Funto'
 */
public class Invoke implements Operator {

	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(Invoke.class);
	
	/**
	 * The name of the method to be invoked.
	 */
	private String methodName;
	
	/**
	 * Constructor.
	 */
	public Invoke() {
	}
	
	/**
	 * Initialises the <code>Invoke</code> operator by supplying the name
	 * of the method to be invoked.
	 * 
	 * @param methodName
	 *   the name of the method to be invoked.
	 */
	public Operator initialise(String methodName) {
		logger.info("operator Invoke for method '{}' ready", methodName);
		this.methodName = methodName;
		return this;
	}
	
	/**
	 * Invokes the method on the given operand.
	 * 
	 * @param operand
	 *   the object whose method will be invoked.
	 * @return 
	 *   the object returned by the method invocation.
	 */
	public Object apply(Object operand) throws Exception {
		assert operand != null : "operand must be a valid object";
		
		return new Reflector(operand).invoke(methodName);
	}

	/**
	 * Returns a string prepresentation of the <code>Operator</code>.
	 */
	public String toString() {
		return "." + methodName + "()";
	}
	
}
