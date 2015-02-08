/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.taglib;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.strutlets.ActionContext;

/**
 * A tag proving access to the portlet request.
 * 
 * @author Andrea Funto'
 */
public class RequestTag extends TagSupport {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -448874943623743789L;

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
	 * Stores the current portlet request into the given variable.
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	@SuppressWarnings("deprecation")
	public int doStartTag() throws JspException {
		PortletRequest request = ActionContext.getPortletRequest();
		pageContext.setAttribute(var, request);
		return EVAL_BODY_INCLUDE;
	}
}
