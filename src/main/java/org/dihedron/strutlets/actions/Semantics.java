/**
 * Copyright (c) 2013, Andrea Funto'. All rights reserved.
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

import org.dihedron.utils.Strings;



/**
 * Indicates the behaviour of the business method: it can change the system's 
 * internal state ("READ/WRITE"), and be thus apt to the execution of business 
 * logic in the context of a action or even processing phase, or be a "READ-ONLY"
 * method, that is supposed not to change the system's internal state.
 * 
 * @author Andrea Funto'
 */
public enum Semantics {
	/**
	 * Used to indicate that the annotated method may change the internal 
	 * system's state. Methods annotated with this value cannot respond to
	 * render requests.
	 */
	READ_WRITE("read/write"),
	
	/**
	 * Used to annotate a read-only method, which is supposed not to change
	 * the system's internal state (e.g. it can read from a database, but
	 * it's supposed not to write to it, as render requests must be 
	 * idempotent and reiterable).
	 */
	READ_ONLY("read-only");
	
	/**
	 * Returns the enumeration item corresponding to the given input string.
	 * 
	 * @param value
	 *   the text representation of the enumeration item; it is evaluated 
	 *   case-insensitively.
	 * @return
	 *   the corresponding enumeration item, or null if none corresponding.
	 */
	public static final Semantics getFor(String value) {
		if(Strings.isValid(value)) {
			for(Semantics semantics : Semantics.values()) {
				if(semantics.value.equalsIgnoreCase(value)) {
					return semantics;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the string representation of the enumeration item.
	 * 
	 * @return
	 *   the string representation of the enumeration item.
	 * @see 
	 *   java.lang.Enum#toString()
	 */
	public String toString() {
		return value;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param value
	 *   the text representation of the enumeration value.
	 */
	private Semantics(String value) {
		this.value = value;
	}
	
	/**
	 * The text representation of the enumeration value. 
	 */
	private String value;
}
