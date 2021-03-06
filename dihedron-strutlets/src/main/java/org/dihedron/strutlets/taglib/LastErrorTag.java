/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
public class LastErrorTag extends TagSupport {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 7648028599796044785L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(LastErrorTag.class);
		
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
