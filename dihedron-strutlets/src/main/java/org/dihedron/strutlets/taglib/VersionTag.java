/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.dihedron.strutlets.Strutlets;

/**
 * Prints out the current Strutlets version.
 * 
 * @author Andrea Funto'
 */
public class VersionTag extends SimpleTagSupport {

	/**
	 * Prints the current Strutlets version to the page.
	 * 
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws IOException { 
		getJspContext().getOut().println("<a href=\"" + Strutlets.getWebSite() + "\">Strutlets " + Strutlets.getVersion() + "</a>");
	}
}
