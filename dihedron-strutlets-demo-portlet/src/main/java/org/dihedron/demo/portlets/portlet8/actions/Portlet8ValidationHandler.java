/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.demo.portlets.portlet8.actions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.validation.ValidationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;

/**
 * @author Andrea Funto'
 */
public class Portlet8ValidationHandler implements ValidationHandler {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Portlet8ValidationHandler.class);

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onParametersViolations(java.util.Set)
	 */
	@Override
	public String onParametersViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
		SessionMessages.add(ActionContext.getPortletRequest(), ActionContext.getPortletName() + SessionMessages. KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on parameter value '{}' will show error message with key '{}'", action, method, violation.getInvalidValue(), violation.getMessage());
			SessionErrors.add(ActionContext.getPortletRequest(), violation.getMessage());
		}		
		return "invalid_input";
	}

	/**
	 * @see org.dihedron.strutlets.validation.ValidationHandler#onResultViolations(java.util.Set)
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
		SessionMessages.add(ActionContext.getPortletRequest(), ActionContext.getPortletName() + SessionMessages. KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
		for(ConstraintViolation<?> violation : violations) {
			logger.warn("{}!{}: violation on model bean {} (no. {}), value {}: {}", action, method, model.getSimpleName(), index, violation.getInvalidValue(), violation.getMessage());
			SessionErrors.add(ActionContext.getPortletRequest(), violation.getMessage());
		}		
		return "invalid_input";
	}	
}
