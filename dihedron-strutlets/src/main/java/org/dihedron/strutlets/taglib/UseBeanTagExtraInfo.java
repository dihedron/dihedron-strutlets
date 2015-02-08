/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Andrea Funto'
 */
public class UseBeanTagExtraInfo extends TagExtraInfo {

	/**
	 * Returns the characteristics of the defined variable: its name is taken from 
	 * the "var" tag attribute, its class from the "type" attribute; the "scope"
	 * attribute indicates:<ul>
	 * <li>if "nested", that the variable will be available only between the start 
	 * and end &lt;useBean&gt; tags;</li>
	 * <li>if "page" (the default), that the variable will be available from the 
	 * point where the (empty) <&lt;useBean<&gt; tag is opened until the end of 
	 * the page.</li>
	 * </ul> 
	 * 
	 * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp.tagext.TagData)
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		String attribute = data.getAttributeString("visibility");
		int visibility = VariableInfo.AT_BEGIN; 
		if(attribute != null && attribute.trim().equalsIgnoreCase("nested")) {
			visibility = VariableInfo.NESTED;
		}
		 
		return new VariableInfo[] { 
			new VariableInfo(
				// the name of the variable is in "var"
				data.getAttributeString("var"), 
				// its type is in "type"
				data.getAttributeString("type"),
				// variable will be declared
				true,
				// and it's available until the end tag
				visibility
			) 
		};
	}
}
