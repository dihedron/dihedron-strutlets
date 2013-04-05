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

import java.io.PrintStream;

/**
 * A class to store general information about the library.
 * 
 * @author Andrea Funto'
 */
public final class Strutlets {
	
	/**
	 * The library version.
	 */
	public final static String VERSION = "0.5.1";

	/** 
	 * The output channel.
	 */
	private static final PrintStream out = System.out;
	
	private static final String HELP_COMMAND = "--help";
	
	/**
	 * Prints out some generic information about the Strutlets framework.
	 * 
	 * @param args
	 *   input arguments, ignored.
	 */
	public static void main(String args[]) {
    	out.println("   +--------------------------------+");
    	out.println(String.format("   |      STRUTLETS ver. %1$-8s   |", Strutlets.VERSION));
    	out.println("   +--------------------------------+");
    	
    	if(args.length == 0 || args[0].equalsIgnoreCase(HELP_COMMAND)) {
    		out.println("\ncommand synopsis:");
    		out.println("\tstrutlets <arguments>");
    		out.println("where:");
    		out.println("\t--help         prints this help message");
    		out.println("\t--version      prints version information");
    		out.println("\t--license      prints license information");
    		out.println("\t--generate     starts the portlet.xml wizard");
    	}
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private Strutlets() {
	}	
}
