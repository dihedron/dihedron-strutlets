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
