/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.taglib;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class DefineObjectsTag extends SimpleTagSupport {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DefineObjectsTag.class);
	
	public void doTag() throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String lifecycle = (String)request.getAttribute(PortletRequest.LIFECYCLE_PHASE);
		
//		pageContext.setAttribute("context", ActionContext.getContext());
		
		logger.trace("current phase is '{}'", lifecycle);
	}
}
