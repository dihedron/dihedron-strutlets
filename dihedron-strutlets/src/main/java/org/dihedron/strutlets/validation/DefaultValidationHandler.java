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
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onParametersViolations(java.util.Set)
	 */
	@Override
	public String onParametersViolations(Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("violation on parameter value {}: {}", violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onResultViolation(java.util.Set)
	 */
	@Override
	public String onResultViolations(Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("violation on return value {}: {}", violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}
}
