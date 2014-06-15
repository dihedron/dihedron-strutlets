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

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Andrea Funto'
 */
public class AdminConsoleURLTagExtraInfo extends TagExtraInfo {

	/**
	 * Returns the characteristics of the defined variable: its name is taken from 
	 * the "var" tag attribute, its class is a String representing an URL (a render
	 * URL actuallly, pointing to the Strutlets Administrative Console), with a 
	 * page scope (from declaration to the end of the JSP). 
	 * 
	 * @see javax.servlet.jsp.tagext.TagExtraInfo#getVariableInfo(javax.servlet.jsp.tagext.TagData)
	 */
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { 
			new VariableInfo(
				// the name of the variable is in "var"
				data.getAttributeString("var"), 
				// its type is String
				String.class.getName(),
				// variable will be declared
				true,
				// and it's available until the end of the page
				VariableInfo.AT_BEGIN
			) 
		};
	}
}
