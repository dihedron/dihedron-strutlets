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
package org.dihedron.commons.variables;

import java.util.Map;

/**
 * @author Andrea Funto'
 */
public class MapBasedValueProvider implements ValueProvider {

	/**
	 * The map of supported variables keys, along with their values.
	 */
	private Map<String, Object> variables;
	
	/**
	 * Constructor.
	 * 
	 * @param variables
	 *   the map of supported variables keys, along with their values.
	 */
	public MapBasedValueProvider(Map<String, Object> variables) {
		this.variables = variables; 
	}

	/**
	 * Returns the String representation of the value corresponding to the given 
	 * key in the map (if available), null otherwise.
	 * 
	 * @see it.bankitalia.sisi.dsvaa.variables.ValueProvider#onVariable(java.lang.String)
	 */
	@Override
	public String onVariable(String variable) {
		String value = null;
		if(variables != null && variables.containsKey(variable)) {
			Object val = variables.get(variable);
			value = (val != null) ? val.toString() : null;
		}
		return value;
	}
}
