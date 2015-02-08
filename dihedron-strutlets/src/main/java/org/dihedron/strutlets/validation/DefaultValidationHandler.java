/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
			logger.warn("{}!{}:violation on parameter value '{}': {}", action, method, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onResultViolation(java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public String onResultViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on return value '{}': {}", action, method, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onModelViolations(java.lang.String, java.lang.String, int, java.lang.Class, java.util.Set)
	 */
	@Override
	public String onModelViolations(String action, String method, int index, Class<?> model, Set<ConstraintViolation<?>> violations) {
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on model bean {} (no. {}), value '{}': {}", action, method, model.getSimpleName(), index, violation.getInvalidValue(), violation.getMessage());
		}		
		return null;
	}
}
