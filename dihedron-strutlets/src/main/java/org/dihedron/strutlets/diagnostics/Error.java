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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing information pertaining to an exception thrown at runtime.
 * 
 * @author Andrea Funto'
 */
public class Error implements Serializable {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -7519593294271451377L;
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Error.class);
	
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
	public Error(Throwable error) {
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
	 * Returns the type of the exception (e.g {@code NullPointerException}.
	 * 
	 * @return
	 *   the type of the exception (e.g {@code NullPointerException}.
	 */
	public String getType() {
		return error.getClass().getSimpleName();
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
	
	/**
	 * Returns the name of the class where the error occurred.
	 * 
	 * @return
	 *   the name of the class where the error occurred.
	 */
	public String getClassName() {
		return error.getStackTrace()[0].getClassName();
	}
	
	/**
	 * Returns the name of the method where the error occurred.
	 * 
	 * @return
	 *   the name of the method where the error occurred.
	 */
	public String getMethodName() {
		return error.getStackTrace()[0].getMethodName();
	}
	
	/**
	 * Returns whether the error occurred in a native method.
	 * 
	 * @return
	 *   whether the error occurred in a native method.
	 */
	public boolean isInNativeMethod() {
		return error.getStackTrace()[0].isNativeMethod();
	}
	
	/**
	 * Returns the name of the source file where the error occurred.
	 * 
	 * @return
	 *   the name of the source file where the error occurred.
	 */	
	public String getSourceFileName() {
		return error.getStackTrace()[0].getFileName();
	}
	
	/**
	 * Returns the number of the line in the source file where the error occurred.
	 * 
	 * @return
	 *   the number of the line in the source file where the error occurred.
	 */	
	public int getSourceLineNumber() {
		return error.getStackTrace()[0].getLineNumber();
	}
	
	/**
	 * Returns the list of nested exceptions.
	 * 
	 * @return
	 *   the list of nested exceptions.
	 */
	public List<Error> getCauses() {
		List<Error> causes = new ArrayList<Error>();
		Throwable current = error;
		while(current.getCause() != null) {
			logger.trace("adding cause of type {}", current.getCause().getClass().getSimpleName());
			causes.add(new Error(current.getCause()));
			current = current.getCause();
		}
		return causes;		
	}	
}
