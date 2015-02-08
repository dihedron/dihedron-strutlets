/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.exceptions;


/**
 * Class of exceptions thrown by the {@code Interceptor}s.
 * 
 * @author Andrea Funto'
 */
public class InterceptorException extends StrutletsException {

	/**
	 * Serial version id. 
	 */
	private static final long serialVersionUID = 3059665510838061449L;

	/**
	 * Constructor. 
	 */
	public InterceptorException() {
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *   the exception message.
	 */
	public InterceptorException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *   the exception's root cause.
	 */
	public InterceptorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 *   the exception message.
	 * @param cause
	 *   the exception's root cause.
	 */
	public InterceptorException(String message, Throwable cause) {
		super(message, cause);
	}
}
