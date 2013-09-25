/**
 * Copyright (c) 2013, Andrea Funto'. All rights reserved.
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
