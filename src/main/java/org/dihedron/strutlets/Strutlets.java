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
 * A class to store general information about the library.
 * 
 * @author Andrea Funto'
 */
public class Strutlets {
	
	/**
	 * The library version.
	 */
	public final static String VERSION = "0.2.0";
	
	/**
	 * Prints out some generic information about the Strutlets framework.
	 * 
	 * @param args
	 *   input arguments, ignored.
	 */
	public static void main(String args[]) {
		System.out.println("Strutlets ver." + VERSION + "\n");
		// TODO: complete with documentation here...
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private Strutlets() {
	}	
}
