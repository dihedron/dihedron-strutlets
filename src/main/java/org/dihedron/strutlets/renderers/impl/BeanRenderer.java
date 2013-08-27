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

package org.dihedron.strutlets.renderers.impl;

import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.dihedron.strutlets.ActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public abstract class BeanRenderer extends AbstractRenderer {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(BeanRenderer.class);
	
	/**
	 * Tries to retrieve the bean from the parameters and the attrobutes.
	 * 
	 * @param request
	 *   the portlet request obejct.
	 * @param data
	 *   the 
	 * @return
	 */
	protected Object getBean(PortletRequest request, String bean) {
		
		logger.trace("trying to retrieve bean '{}'...", bean);
		Object object = getParameterValues(request, bean);
		if(object != null) {
			logger.trace("... bean found among parameters");
			return object;
		}

		object = getRequestAttribute(request, bean);
		if(object != null) {
			logger.trace("... bean found among request attributes");
			return object;
		}
		
		object = getPortletAttribute(request, bean);
		if(object != null) {
			logger.trace("... bean found among portlet attributes");
			return object;
		}
		
		object = getApplicationAttribute(request, bean);
		if(object != null) {
			logger.trace("... bean found among application attributes");
			return object;
		}
		logger.error("... bean not found");
		return null;
	}
	
	/**
	 * Returns the set of values associated with the given parameter key.
	 * 
	 * @param request
	 *   the portlet request object.
	 * @param key
	 *   the name of the parameter.
	 * @return
	 *   the array of parameter values.
	 */
	protected String[] getParameterValues(PortletRequest request, String key) {
		return request.getParameterValues(key);
	}	

	/**
	 * Returns the value of the request-scoped attribute.
	 * 
	 * @param request
	 *   the portlet request object.
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the value of the request-scoped attribute, or null if not set.
	 */
	protected Object getRequestAttribute(PortletRequest request, String key) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>)getPortletAttribute(request, ActionContext.getRequestScopedAttributesKey()); 
		Object value = map.get(key);
		logger.trace("request attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : "<null>");
		return value;
	}
	
	/**
	 * Returns the portlet-scoped attribute corresponding to the given key. 
	 * 
	 * @param request
	 *   the portlet request object.
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	protected Object getPortletAttribute(PortletRequest request, String key) {
		PortletSession session = request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.PORTLET_SCOPE);
		logger.trace("portlet attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : "<null>");
		return value;
	}
	
	/**
	 * Returns the application-scoped attribute corresponding to the given key. 
	 * 
	 * @param request
	 *   the portlet request object.
	 * @param key
	 *   the attribute key.
	 * @return
	 *   the attribute value.
	 */	
	protected Object getApplicationAttribute(PortletRequest request, String key) {
		PortletSession session = request.getPortletSession();
		Object value = session.getAttribute(key, PortletSession.APPLICATION_SCOPE);
		logger.trace("application attribute '{}' has value '{}' (class '{}')", key, value, value != null ? value.getClass().getSimpleName() : "<null>");
		return value;
	}	
}
