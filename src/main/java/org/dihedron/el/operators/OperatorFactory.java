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

import java.util.List;

import org.dihedron.regex.Regex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory use to create new <code>operato</code> instances.
 * 
 * @author Andrea Funto'
 */
public class OperatorFactory {

	/**
	 * The regular expression pattern representing the root object selector.
	 */
	public static final String ROOT_SELECTOR_PATTERN = "^\\s*#\\s*\\[\\s*'\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*'\\s*\\]\\s*$";

	/**
	 * The regular expression pattern representing a field accessor.
	 */
	public static final String FIELD_ACCESS_PATTERN = "^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*$";
	
	/**
	 * The regular expression pattern representing a method invocation.
	 */
	public static final String INVOCATION_PATTERN = "^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(\\s*\\)\\s*$";
	
	/**
	 * The regular expression pattern representing an indexed array access.
	 */
	public static final String INDEXED_ACCESS_PATTERN = "^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\[\\s*(\\d*)\\s*\\]\\s*$";
	
	/**
	 * The regular expression pattern representing an keyed map access.
	 */
	public static final String KEYED_ACCESS_PATTERN = "^\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\[\\s*'\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*'\\s*\\]\\s*$";

	/**
	 * The regular expression object representing the root selection operator.
	 */
	private static final Regex ROOT_SELECTOR_REGEXP = new Regex(ROOT_SELECTOR_PATTERN);

	/**
	 * The regular expression object representing a field accessor.
	 */
	private static final Regex FIELD_ACCESS_REGEXP = new Regex(FIELD_ACCESS_PATTERN);
	
	/**
	 * The regular expression object representing a method invocation.
	 */
	private static final Regex INVOCATION_REGEXP = new Regex(INVOCATION_PATTERN);
	
	/**
	 * The regular expression object representing an indexed array accessn.
	 */
	private static final Regex INDEXED_ACCESS_REGEXP = new Regex(INDEXED_ACCESS_PATTERN);

	/**
	 * The regular expression object representing a keyed map access.
	 */
	private static final Regex KEYED_ACCESS_REGEXP = new Regex(KEYED_ACCESS_PATTERN);
	
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(OperatorFactory.class);
	
	/**
	 * The operator that picks up the root element; by default it is the
	 * <code>Identity</code> operator.
	 */
	private Selector selector = new Identity();
	
	/**
	 * Sets a reference to the object used to select the root object.
	 * By default this object is the <code>Identity</code>, which binds
	 * the expression to the same object passed in as <code>operand</code>.
	 *  
	 * @param selector
	 *   the <code>Selector</code> object or a subclass.
	 */
	public void setRootSelector(Selector selector) {
		this.selector = selector;
	}
	
	/**
	 * Creates one or more <code>Operator</code> according to the regular
	 * expression pattern mathcing.
	 * 
	 * @param token
	 *   the token representing a sequence of one or more <code>Operator</code>s.
	 * @return
	 *   an array of <code>Operator</code>s.
	 */
	public Operator[] makeOperators(String token) {
		Operator [] operators = null;
		if(ROOT_SELECTOR_REGEXP.matches(token)) {
			logger.info("'{}' is the root selection operator", token);
			operators = makeRootSelector(token);
		} else if(INVOCATION_REGEXP.matches(token)) {
			logger.info("'{}' is a method call", token);
			operators = makeInvoke(token);
		} else if(INDEXED_ACCESS_REGEXP.matches(token)) {
			logger.info("'{}' is an array indexed access", token);
			operators = makeIndexedAccess(token);
		} else if(KEYED_ACCESS_REGEXP.matches(token)) {
			logger.info("'{}' is a keyed map access", token);
			operators = makeKeyedAccess(token);
		} else if(FIELD_ACCESS_REGEXP.matches(token)) {
			logger.info("'{}' is an object", token);
			operators = makeAccessor(token);
		}
		return operators;
	}
	
	/**
	 * Returns an array containing the <code>RootSelector</code> operator.
	 *  
	 * @param token
	 *   a string representing the root selctor operator.
	 * @return
	 *   an array containing the <code>RootSelector</code> operator.
	 */
	Operator[] makeRootSelector(String token) {
		List<String[]> matches = ROOT_SELECTOR_REGEXP.getAllMatches(token);
		String [] values = matches.get(0);
		return new Operator[] { selector.initialise(values[0]) };
		
	}

	/**
	 * Returns an array containing the new <code>GetField</code> operator.
	 *  
	 * @param token
	 *   a string representing a field accessor operator.
	 * @return
	 *   an array containing the new <code>GetField</code> operator.
	 */
	Operator[] makeAccessor(String token) {
		List<String[]> matches = FIELD_ACCESS_REGEXP.getAllMatches(token);
		String [] values = matches.get(0);
		return new Operator[] { new FieldAccess().initialise(values[0]) };
		
	}

	/**
	 * Returns an array containing the new <code>Invoke</code> operator.
	 *  
	 * @param token
	 *   a string representing a method invocation operator.
	 * @return
	 *   an array containing the new <code>Invoke</code> operator.
	 */
	Operator[] makeInvoke(String token) {
		List<String[]> matches = INVOCATION_REGEXP.getAllMatches(token);
		String [] values = matches.get(0);
		return new Operator[] { new Invoke().initialise(values[0]) };
	}

	/**
	 * Returns an array containing the new <code>GetAt</code> operator.
	 *  
	 * @param token
	 *   a string representing an indexed array access operator.
	 * @return
	 *   an array containing the new <code>FieldAccess</code> and <code>
	 *   indexedAccess</code> operators.
	 */
	Operator[] makeIndexedAccess(String token) {
		List<String[]> matches = INDEXED_ACCESS_REGEXP.getAllMatches(token);
		String [] values = matches.get(0);
		return new Operator[] { 
				new FieldAccess().initialise(values[0]), 
				new IndexedAccess().initialise(values[1]) 
			};
	}
	
	/**
	 * Returns an array containing the new <code>GetAt</code> operator.
	 *  
	 * @param token
	 *   a string representing an indexed array access operator.
	 * @return
	 *   an array containing the new <code>FieldAccess</code> and <code>
	 *   KeyedAccess</code> operators.
	 */
	Operator[] makeKeyedAccess(String token) {
		List<String[]> matches = KEYED_ACCESS_REGEXP.getAllMatches(token);
		String [] values = matches.get(0);
		return new Operator[] { 
				new FieldAccess().initialise(values[0]), 
				new KeyedAccess().initialise(values[1]) 
			};
	}	
}
