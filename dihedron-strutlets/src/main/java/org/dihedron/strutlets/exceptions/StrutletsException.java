/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.exceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.portlet.PortletException;

/**
 * Base exception of all Strutlets exceptions.
 * 
 * @author Andrea Funto'
 */
public class StrutletsException extends PortletException {

	/**
	 * Serial verion id.
	 */
	private static final long serialVersionUID = -8305119928892480615L;

	/**
	 * Constructor.
	 */
	public StrutletsException() {
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *   the exception message.
	 */
	public StrutletsException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *   the exception's root cause.
	 */
	public StrutletsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *   
	 * @param message
	 *   the exception message.
	 * @param cause
	 *   the exception's root cause.
	 */
	public StrutletsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Prints the exception's stack trace to a String. 
	 */
	public String getStackTraceAsString() {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		this.printStackTrace(printWriter);
		return writer.toString();		
	}
}
