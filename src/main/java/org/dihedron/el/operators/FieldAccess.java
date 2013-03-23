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
 * Implements the field retrieval, in either of the following ways: <ol>
 * <li> by accessing the field directly through reflection: if the field is
 * not publicly accessible it is made so for a short while, read and then protected 
 * again through use of reflection.</li>
 * <li> by calling the corresponding getter method </li>
 * </ol>
 * 
 * @author Andrea Funto'
 */
public class FieldAccess implements Operator {

	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(FieldAccess.class);

	/**
	 * Whether the field access should go through the getter.
	 */
	private static boolean useGetter = false;
	
	/**
	 * Sets the global property driving the choice whether the field retrieval
	 * should go through the getter.
	 * 
	 * @param value
	 *   <code>true</code> to force access through the getter method, <code>false
	 *   </code> to get direct access to the field.
	 */
	public static void setUseGetter(boolean value) {
		useGetter = value;
	}
	
	/**
	 * The name of the firld to access.
	 */
	private String fieldName;
	
	/**
	 * Constructor.
	 */
	public FieldAccess() {
	}	
	
	/**
	 * Initialises the <code>FieldAccess</code> operator by supplying the name
	 * of the field to be accessed
	 * 
	 * @param fieldName
	 *   the name of the field to be accessed.
	 */
	public Operator initialise(String fieldName) {
		logger.info("operator FieldAccess for field '{}' ready", fieldName);
		this.fieldName = fieldName;
		return this;
	}
	
	/**
	 * Applies the operator to the given operand, and returns the field
	 * value.
	 */
	public Object apply(Object operand) throws Exception {
		assert(operand != null);
		logger.info("getting field '{}'", fieldName);
		return new Reflector(operand, useGetter).getFieldValue(fieldName);
	}
	
	/**
	 * Provides a string representation of the operator.
	 * 
	 * @return
	 *   a string representation of the operator
	 */
	public String toString() {
		return "." + fieldName;
	}
}
