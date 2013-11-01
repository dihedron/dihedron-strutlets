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
package org.dihedron.strutlets.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;


/**
 * The base interface to all validation callback handlers.
 * 
 * @author Andrea Funto'
 */
public interface Validator<T> {
	
	String onParametersViolations(Set<ConstraintViolation<T>> violations);
	
	String onResultViolation(Set<ConstraintViolation<T>> violations);
	
	String onMethodViolations(Set<ConstraintViolation<T>> violations);

	/**
	 * A dummy class to represent no validator at all; this is necessary to 
	 * circumvent a limitation in the annotation specification whereby null is
	 * not a valid value for annotation attributes.  
	 * 
	 * @author Andrea Funto'
	 */
	public static final class NONE implements Validator<Void>{

		/**
		 * @see org.dihedron.strutlets.validation.Validator#onParametersViolations(java.util.Set)
		 */
		@Override
		public String onParametersViolations(Set<ConstraintViolation<Void>> violations) {
			return null;
		}

		/**
		 * @see org.dihedron.strutlets.validation.Validator#onResultViolation(java.util.Set)
		 */
		@Override
		public String onResultViolation(Set<ConstraintViolation<Void>> violations) {
			return null;
		}

		/**
		 * @see org.dihedron.strutlets.validation.Validator#onMethodViolations(java.util.Set)
		 */
		@Override
		public String onMethodViolations(Set<ConstraintViolation<Void>> violations) {
			return null;
		}		
	} 
}
