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
	 * system's state as part of its business logic; moreover methods annotated
	 * with this value may have access to events, render parameters and all the
	 * goodies made available to portlets through {@code StateAwareResponse}s.
	 * Methods annotated with this value can be used to respond to action and 
	 * event requests; thy <em>must not</em> be used to respond to render requests.
	 */
	BUSINESS("business"),
	
	/**
	 * Used to annotate a method to be used exclusively in the render phase.
	 * These methods should be able to be invoked as many times as the container
	 * sees fit while still keeping the internal system coherence. This means
	 * that the methods should be <em>idempotent</em> and <em>repeatabale</em>, 
	 * that is it can be called multiple times in a row and it should always 
	 * render consistent and valid results. This does not necessarily mean that 
	 * it cannot refresh the page contents, perform new queries, etc.: it can
	 * interact with the model and even update the system status, but it must
	 * do so in a way that guarantees that the order and number of invocations
	 * does not lead to unpredictable internal system status.
	 * Moreover, methods annotated as "presentation" and used duing the render 
	 * phase have no access to render parameters and events: trying to call
	 * methods that set render parameters or fire events will result in an 
	 * exception being thrown.  
	 */
	PRESENTATION("presentation");
	
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
