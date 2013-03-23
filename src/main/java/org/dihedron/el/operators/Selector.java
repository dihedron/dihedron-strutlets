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

import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the selection of the root element in the obejct graph; each extending
 * subclass will provide the custom implementation for binding the navigation
 * expression to the root object. 
 * 
 * @author Andrea Funto'
 */
public abstract class Selector implements Operator {

	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Selector.class);
	
	/**
	 * Information pertaining to the way to retrieve the root object.
	 */
	protected String info;
	
	/**
	 * Initialises the operator, by storing the information about
	 * how to retrieve the root obkject from the
	 * 
	 * @param info
	 *   information about how to bind the root element.
	 */
	public Operator initialise(String info) {
		logger.info("operator Select for context '{}' ready", info);
		this.info = info;	
		return this;
	}
	
	/**
	 * Applies the operator to the given input parameter.
	 * 
	 * @param context
	 *   the object to which the operator will be applied; the root 
	 *   element will be picked from this context, according to rules 
	 *   implemented by subclasses.
	 */
	public abstract Object apply(Object context) throws StrutletsException;	
	
	/**
	 * Returns a representation of the operator as a String.
	 */
	public String toString() {
		return "#['" + info + "']";
	}
}
