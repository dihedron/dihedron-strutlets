/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.taglib;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Andrea Funto'
 */
public class ResponseTagExtraInfo extends TagExtraInfo {

	/**
	 * Returns the characteristics of the defined variable: its name is taken from 
	 * the "var" tag attribute, its class is that of the PortletResponse class (see
	 * {@link PortletRequest}), with a page scope (from declaration to the end
	 * of the JSP). 
	 * 
	 * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp.tagext.TagData)
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { 
			new VariableInfo(
				// the name of the variable is in "var"
				data.getAttributeString("var"), 
				// its type is that of the PortletResponse class (or a subclass thereof)
				PortletResponse.class.getName(),
				// variable will be declared
				true,
				// and it's available until the end of the page
				VariableInfo.AT_BEGIN
			) 
		};
	}
}
