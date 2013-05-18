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

package org.dihedron.strutlets.actions;


/**
 * An enumeration representing the possible types of results for actions.
 * 
 * @author Andrea Funto'
 */
public enum ResultType {
	
	/**
	 * The result is a JSP file.
	 */
	JSP("jsp"),
	
	/**
	 * The result is an object to be rendered in JSON.
	 */
	JSON("json"),
	
	/**
	 * The result is an object to be rendered in XML.
	 */
	XML("xml"),
	
	/**
	 * The result is to be passed as is to the caller.
	 */
	RAW("raw");
		
	/**
	 * Converts a string representation of the result type into an enumeration 
	 * value, if possible, otherwise throws an exception.
	 * 
	 * @param string
	 *   a textual representation of the result type whose corresponding enumeration 
	 *   value is to be retrieved.
	 * @return
	 *   the enumeration value; if none matches, an exception is thrown.
	 */
	public static final ResultType fromString(String string) {
		if(string != null) {
			for(ResultType type : ResultType.values()) {
				if(type.type.equalsIgnoreCase(string)) {
					return type;
				}
			}
		}
		throw new IllegalArgumentException("'" + string + "' is not a valid value for result types");
	}
	
	/**
	 * Returns a textual representation of the type.
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		return type;
	}

	/**
	 * Constructor.
	 *
	 * @param type
	 *   the textual representation of this result type.
	 */
	private ResultType(String type) {
		this.type = type;
	}

	/**
	 * The textual representation of this XML type.
	 */
	private String type;
}
