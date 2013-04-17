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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author Andrea Funto'
 */
public class DefineObjectsHandler extends TagSupport {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 6007222483445909571L;

	@Override
	public int doStartTag() throws JspException {

//		try {
			// get the writer object for output
			JspWriter out = pageContext.getOut();
			
			pageContext.setAttribute("pippo", new String("ciao"));

			// Perform substr operation on string.
			//out.println(input.substring(start, end));

//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return SKIP_BODY;
	}

}
