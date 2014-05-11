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
package org.dihedron.strutlets;


/**
 * A class to store constant values.
 * 
 * @author Andrea Funto'
 */
public final class Constants {
	
	/**
	 * The number of bytes in a kilobyte.
	 */
	public static final int KILOBYTE = 1024;
	
	/**
	 * The number of bytes in a megabyte.
	 */
	public static final int MEGABYTE = 1024 * KILOBYTE; 

	/**
	 * The number of bytes in a gigabyte.
	 */
	public static final int GIGABYTE = 1024 * MEGABYTE; 
	
	public static final String PORTLETS_TEMP_DIR_ATTRIBUTE = "javax.servlet.context.tempdir";
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private Constants() {
	}	
}
