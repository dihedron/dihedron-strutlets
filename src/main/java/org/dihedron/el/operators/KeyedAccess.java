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
 * Implements the retrieval of the value corresponding to a given <code>String</code> 
 * key from a <code>Map&lt;String, Object&gt;</code>. 
 * 
 * @author Andrea Funto'
 */
public class KeyedAccess implements Operator {
	
	/**
	 * The logger
	 */
	private final static Logger logger = LoggerFactory.getLogger(KeyedAccess.class);
	
	/**
	 * The key of the <code>Map</code> element to retrieve.
	 */
	private String key = null;
		
	/**
	 * Constructor.
	 */
	public KeyedAccess() {
	}
	
	/**
	 * Initialises the <code>KeyedAccess</code> operator by supplying the key
	 * of the element to be accessed.
	 * 
	 * @param key
	 *   the key of the element to be accessed.
	 */
	public Operator initialise(String key) {
		logger.info("operator KeyedAccess for offset '{}' ready", key);
		this.key = key;
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
		return new Reflector(operand).getValueForKey(key);
	}
	
	/**
	 * Returns a representation of the operator as a String.
	 */
	public String toString() {
		return "['" + key + "']";
	}
}
