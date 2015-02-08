/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.taglib;

import javax.portlet.PortletURL;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.Strutlets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tag that stores the address of the Strutlets Administrative Console into 
 * a variable in the page context.
 * 
 * @author Andrea Funto'
 */
public class AdminConsoleURLTag extends TagSupport {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 4990186274074140451L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(AdminConsoleURLTag.class);
		
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
	 * Creates a RenderURL to the Strutlets Administrative Console and stores it 
	 * under the given name in the page context. 
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		PortletURL adminConsoleURL = ActionContext.createRenderURL();
		adminConsoleURL.setParameter(Strutlets.STRUTLETS_TARGET, "StrutletsAdminConsole!render");
		logger.trace("storing admin console URL into page context");
		pageContext.setAttribute(var, adminConsoleURL.toString());
		return EVAL_BODY_INCLUDE;
	}
}
