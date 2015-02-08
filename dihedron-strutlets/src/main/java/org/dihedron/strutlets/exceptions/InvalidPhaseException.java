/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.exceptions;


/**
 * An exception thrown whenever an operation is attempted that should not be 
 * performed in the current portlet life cycle phase. An instance of such errors
 * is when a render parameter is set in the RENDER phase.
 * 
 * @author Andrea Funto'
 */
public class InvalidPhaseException extends ActionException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 3549200585726515802L;

	/**
	 * Constructor.
	 */
	public InvalidPhaseException() {
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *   the exception message.
	 */
	public InvalidPhaseException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param cause
	 *  the exception's root cause.
	 */
	public InvalidPhaseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *   the exception's message.
	 * @param cause
	 *   the exception's root cause.
	 */
	public InvalidPhaseException(String message, Throwable cause) {
		super(message, cause);
	}
}
