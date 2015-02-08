/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.exceptions;


/**
 * An exception thrown when the application is is not fully configured. It might
 * be thrown when no action could be found for a given client request, or when 
 * the processing of the action yields a result value for which no rebder URL
 * is available or can be inferred.
 *  
 * @author Andrea Funto'
 */
public class InvalidConfigurationException extends StrutletsException {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 512988282567394796L;

	/**
	 * Constructor.
	 */
	public InvalidConfigurationException() {
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *   the exception message.
	 */
	public InvalidConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *   the root cause of the exception.
	 */
	public InvalidConfigurationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *   the exception message.
	 * @param cause
	 *   the root cause of the exception.
	 */
	public InvalidConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
