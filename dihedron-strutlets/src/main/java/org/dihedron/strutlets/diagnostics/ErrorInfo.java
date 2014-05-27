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
package org.dihedron.strutlets.diagnostics;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * A class representing information pertaining to an exception thrown at runtime.
 * 
 * @author Andrea Funto'
 */
public class ErrorInfo implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -7519593294271451377L;
	
	/**
	 * The actual exception being shown.
	 */
	private Throwable error;
	
	/**
	 * Constructor.
	 * 
	 * @param error
	 *   the exception for which a diagnostic context is being created.
	 */
	public ErrorInfo(Throwable error) {
		this.error = error;
	}
	
	/**
	 * Returns the error message associated with the exception.
	 * 
	 * @return
	 *   the error message associated with the exception.
	 */
	public String getMessage() {
		return error.getMessage();
	}
	
	/**
	 * Returns the stack trace associated with the exception.
	 * 
	 * @return
	 *   the stack trace associated with the exception.
	 */
	public String getStackTrace() {
		StringWriter writer = new StringWriter();
		error.printStackTrace(new PrintWriter(writer));
		return writer.toString();
	}
}
