/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
