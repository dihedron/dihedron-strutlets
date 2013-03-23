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
 * Implements the retrieval of the n-th element on an array or an object
 * implementing the <code>List</code> interface. 
 * 
 * @author Andrea Funto'
 */
public class IndexedAccess implements Operator {
	
	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(IndexedAccess.class);
	
	/**
	 * The index of the element to be retrieved. 
	 */
	private int index = -1;

	/**
	 * Constructor.
	 */
	public IndexedAccess() {
	}
	
	/**
	 * Initialises the <code>IndexedAccess</code> operator by supplying the index
	 * of the element to be accessed.
	 * 
	 * @param index
	 *   the index of the element to be accesed.
	 */
	public Operator initialise(String index) {
		logger.info("operator IndexedAccess for offset '{}' ready", index);
		this.index = Integer.parseInt(index);
		return this;
	}

	/**
	 * Initialises the <code>IndexedAccess</code> operator by supplying the index
	 * of the element to be accessed.
	 * 
	 * @param index
	 *   the index of the element to be accesed.
	 */
	public Operator initialise(int index) {
		logger.info("operator IndexedAccess for offset '{}' ready", index);
		this.index = index;
		return this;
	}
	
	/**
	 * Applies the operator to the given input parameter.
	 * 
	 * @param operand
	 *   the object to which the operator will be applied.
	 */
	public Object apply(Object operand) throws Exception {		
		if(operand == null) {
			logger.error("operand must be a valid object");
			throw new Exception("operand must be a valid object");
		}
		return new Reflector(operand).getElementAtIndex(index);
	}
	
	/**
	 * Returns a representation of the operator as a String.
	 */
	public String toString() {
		return "[" + index + "]";
	}
}
