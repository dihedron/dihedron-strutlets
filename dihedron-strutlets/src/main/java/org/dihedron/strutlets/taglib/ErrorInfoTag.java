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
package org.dihedron.strutlets.taglib;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.Strutlets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tag proving access to the exception thrown during the processing (if any).
 * 
 * @author Andrea Funto'
 */
public class ErrorInfoTag extends TagSupport {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7648028599796044785L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ErrorInfoTag.class);
		
	/**
	 * The name of the destination variable.
	 */
	private String var;
			
	/**
	 * Sets the name of the destination variable.
	 * 
	 * @param var
	 *   the name of the destination variable.
	 */
	public void setVar(String var) {
		this.var = var;
	}
		
	/**
	 * Retrieves the error info from the request scope and makes it available 
	 * as a variable in the page context.
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public int doStartTag() throws JspException {
		String keyName = ActionContext.getRequestScopedAttributesKey();
		Object value = getAttribute(keyName, PortletSession.PORTLET_SCOPE);
		if(value != null) {							
			Map<String, Object> map = (Map<String, Object>)value;
			value = map.get(Strutlets.STRUTLETS_ERROR_INFO);				
			logger.trace("storing error info into page context");
			pageContext.setAttribute(var, value);
		}
		return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * Retrieves an attribute from the given scope; in doing so, it decodes its name
	 * and scope (according to the JSR-286 naming conventions and by using the 
	 * appropriate {@code PortletSessionUtil} methods in order to be completely
	 * compliant with the standard.
	 * 
	 * @param name
	 *   the (non-decorated) name of the attribute.
	 * @param scope
	 *   the scope of the attribute.
	 * @return
	 *   the attribute, if found; null otherwise.
	 */
	private Object getAttribute(String name, int scope) {
		logger.trace("looking for attribute '{}' in scope '{}'...", name, scope);
		HttpSession session = pageContext.getSession();
		@SuppressWarnings("unchecked")
		Enumeration<String> names = (Enumeration<String>)session.getAttributeNames();
		while(names.hasMoreElements()) {
			String encodedName = names.nextElement();
			String decodedName = PortletSessionUtil.decodeAttributeName(encodedName);			
			int decodedScope = PortletSessionUtil.decodeScope(encodedName);
			logger.trace(" ... analysing attribute '{}' (encoded: '{}') in scope '{}'", decodedName, encodedName, decodedScope);
			if(decodedName.equals(name) && decodedScope == scope) {
				logger.trace("attribute '{}' found in PORTLET scope", decodedName);
				return session.getAttribute(encodedName);
			}				
		}
		return null;
	}
}
