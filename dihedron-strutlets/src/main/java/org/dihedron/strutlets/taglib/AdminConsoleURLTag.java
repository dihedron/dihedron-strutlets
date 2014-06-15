/**
 * Copyright (c) 2012, 2014, Andrea Funto'. All rights reserved.
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
