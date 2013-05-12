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
