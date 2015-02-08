/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.taglib;

import javax.portlet.PortletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.strutlets.ActionContext;

/**
 * A tag proving access to the portlet response.
 * 
 * @author Andrea Funto'
 */
public class ResponseTag extends TagSupport {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8181962685096796503L;
	
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
	 * Stores the current portlet response into the given variable. 
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	@SuppressWarnings("deprecation")
	public int doStartTag() throws JspException {
		PortletResponse response = ActionContext.getPortletResponse();
		pageContext.setAttribute(var, response);
		return EVAL_BODY_INCLUDE;
	}
}
