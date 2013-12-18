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
 * This class provides also a mechanism to keep track of a possible ambiguity
 * with the semantics of the {@code null} value: as a matter of fact in the 
 * context of target invocation, a {@code null} value may mean two different 
 * things:<ol>
 * <li>the method (the target) could not find a meaningful value for the referenced
 * object, but it expects the original value (if any) to be kept in scope</li>
 * <li>the method (the target) wnats its value (be it {@code code} or a meaningful 
 * non-null value) to override any pre-existing value in any scope.</li></ol>
 * By using the {@link #override(boolean)} method you can force the 
 * 
 * @author Andrea Funto'
 */
public class $<T> {

	/**
	 * The reference to the underlying object. 
	 */
	private T reference;
	
	/**
	 * Whether the {@code null} value should override a valid value in the
	 * output scope; by default, it will not.
	 */
	private boolean reset = false;
	
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
	 * @return
	 *   a reference to this very object wrapper, for method chaining.
	 */
	public $<T> set(T reference) {
		this.reference = reference;
		return this;
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
	 * Sets how the framework should behave with respect to null (unbound) 
	 * references: if the {@link #reset(boolean)} method has been called with a 
	 * {@code true} value, the null value will be regarded as meaningful and 
	 * will end up replacing the original value in the output scope; otherwise 
	 * the framework will leave any pre-existing value untouched (the default 
	 * behaviour).
	 *  
	 * @param reset
	 *   whether the framework should reset any pre-existing values in the several
	 *   scopes, thus leaving it unbound if no valid output is provided.
	 * @return
	 *   the object itself, to enable method chaining.
	 */
	public $<T> reset(boolean reset) {
		this.reset = reset;
		return this;
	}
	
	/**
	 * Returns whether the framework should reset a pre-existing value in one of 
	 * the output scopes before processing an output annotation.
	 * 
	 * @return
	 *   whether the output value will override the existing value in the output 
	 *   scope, even if null.
	 */
	public boolean isReset() {
		return reset;
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
	
	/**
	 * This method helps resolve an ambiguity with the singular value {@code null},
	 * which may mean two different things in the context of a target invocation: 
	 * it may stand for "no value found, leave things the way they are", or it
	 * may stand for "the value is <em>actually</em> {@code null}, store it as 
	 * such into the appropriate scope".
	 * By using the {@link #setOverride(boolean)} method, you are instructing the
	 * framework to override the value in the output scope even with a {@code null}
	 * value.
	 * 
	 * @param override
	 *   {@code true} to let this value take precedence over any existing value
	 *   in the output scope, false to leave the output scope as is should this 
	 *   value be {@code null}.
	 * @return
	 *   the wrapper object itself, to enable method chaining.
	 * @deprecated
	 *   use #
	 */
	@Deprecated
	public $<T> setOverride(boolean override) {
		return reset(override);
	}
	
	/**
	 * If {@code true}, the framework will assume {@code null} to be a meaningful
	 * value and will store it in the output scope, overriding any existing value
	 * that might have been there before the target invocation.
	 * 
	 * @return
	 *   whether {@code null} will be considered meaningful or not.
	 * @deprecated
	 *   use
	 */
	@Deprecated
	public boolean isOverride() {
		return this.reset;
	}
	
	@Override
	public String toString() {
		return (isReset() ? "overriding " : "") + "reference: " + (isBound() ? this.reference.toString() : "unbound");
	}
}
