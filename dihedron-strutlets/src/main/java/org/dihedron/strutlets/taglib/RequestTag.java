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
