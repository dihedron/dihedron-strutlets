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

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class UseBeanTag extends TagSupport {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(UseBeanTag.class);
	
	private static final String SCOPE_RENDER = "render";
	
	private static final String SCOPE_REQUEST = "render";
	
	private static final String SCOPE_SESSION = "render";
	
	private static final String SCOPE_APPLICATION = "render";

	/**
	 * The name of the attribute to be made available to the page and EL. 
	 */
	private String name;
	
	/**
	 * The scope in which the attribute/parameter is supposed to be available.
	 */
	private String scope;
	
	/**
	 * The name of the destination variable.
	 */
	private String var;
	
	/**
	 * The class (type) of the destination variable.
	 */
	private String type;
	
	/**
	 * Sets the name of the attribute to be made available to the page and EL.
	 * 
	 * @param name
	 *   the name of the attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the scope in which the attribute/parameter is supposed to be available.
	 * 
	 * @param scope
	 *   the name of the scope; supported values include:<ul>
	 *   <li>render</li>: one of the render parameters;
	 *   <li>request</li>: the bean is among the request attributes;
	 *   <li>session</li>: the bean is among the session attributes;
	 *   <li>application</li>: the bean is among the application attributes;
	 * <ul>
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}
	
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
	 * Sets the type of the destination variable.
	 * 
	 * @param type  
	 *   the type of the destination variable.
	 */
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		//String lifecycle = (String)request.getAttribute(PortletRequest.LIFECYCLE_PHASE);
		
//		if(scope.equalsIgnoreCase())
//		pageContext.get
		
		pageContext.setAttribute(var, "valore di " + var);
		
		return EVAL_BODY_INCLUDE;
	}
}
