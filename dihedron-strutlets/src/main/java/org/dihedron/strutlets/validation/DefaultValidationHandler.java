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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class DefaultValidationHandler implements ValidationHandler {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DefaultValidationHandler.class);

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onParametersViolations(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public String onParametersViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}:violation on parameter value {}: {}", action, method, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onResultViolation(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public String onResultViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on return value {}: {}", action, method, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onModelViolations(java.lang.String, java.lang.String, int, java.lang.Class, java.util.Set)
	 */
	@Override
	public String onModelViolations(String action, String method, int index, Class<?> model, Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on model bean {} (no. {}), value {}: {}", action, method, model.getSimpleName(), index, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}
}
