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

package org.dihedron.strutlets.aop;


/**
 * A holder class, providing a safe mechanism to pass results out of action 
 * methods.
 * 
 * @author Andrea Funto'
 */
public class $<T> {

	/**
	 * The reference to the underlying object. 
	 */
	private T reference;
	
	/**
	 * Constructor.
	 */
	public $() {
		this(null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param reference
	 *   the referenced object.
	 */
	public $(T reference) {
		this.reference = reference;
	}
	
	/**
	 * Sets the reference to the wrapped object.
	 * 
	 * @param reference
	 *   the new reference to the wrapped object.
	 */
	public void set(T reference) {
		this.reference = reference;
	}
	
	/**
	 * Returns the reference to the wrapped object.
	 * 
	 * @return
	 *   the reference to the wrapped object.
	 */
	public T get() {
		return this.reference;
	}
	
	/**
	 * Checks whether the reference is initialised and bound to some object or 
	 * it is still dangling. 
	 * 
	 * @return
	 *   whether the reference point to a valid object.
	 */
	public boolean isBound() {
		return this.reference != null;
	}
	
	/**
	 * Returns whether the internal reference does not point to a valid object 
	 * yet (it is not bound to any object).
	 * 
	 * @return
	 *   whether the internal reference does not point to a valid object yet.
	 */
	public boolean isUnbound() {
		return this.reference == null;
	}
	
	@Override
	public String toString() {
		return "reference: " + (isBound() ? this.reference.toString() : "unbound");
	}
}
