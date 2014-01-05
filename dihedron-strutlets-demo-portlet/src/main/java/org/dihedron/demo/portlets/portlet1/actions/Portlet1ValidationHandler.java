/**
 * Copyright (c) 2012-2014, Andrea Funto'. All rights reserved.
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

package org.dihedron.demo.portlets.portlet1.actions;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.validation.ValidationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;

/**
 * @author Andrea Funto'
 */
public class Portlet1ValidationHandler implements ValidationHandler {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Portlet1ValidationHandler.class);

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
		return null;
	}
}
