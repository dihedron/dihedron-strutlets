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

import javax.portlet.ActionRequest;

/**
 * A class to store general information about the library.
 * 
 * @author Andrea Funto'
 */
public final class Strutlets {
	
	/**
	 * The library version.
	 */
	public static final String VERSION = "0.11.0";
	
	/**
	 * The name of the parameter under which the requested action's name is stored
	 * in the <code>ActionRequest</code> parameters map. 
	 */
	public static final String PORTLETS_TARGET = ActionRequest.ACTION_NAME;
	
	/**
	 * This parameter is used by Liferay to create render requests that navigate
	 * directly to the given URL; in order to be compatible with Liferay's
	 * default JSPs, this parameter is checked before redirecting the client
	 * to the default home page; this parameter may contain the indication of
	 * a Strutlets target (action + method).  
	 */
	public static final String LIFERAY_TARGET = "jspPage";	
	
	/**
	 * The parameter used to pass information about the last action/event execution
	 * to the render phase. This parameter is internal to the framework and should 
	 * not be used outside of it (e.g in render URLs). 
	 */
	public static final String STRUTLETS_TARGET = "org.dihedron.strutlets.action";
	
	/**
	 * The name of the session attribute under which the action's result is stored.
	 * This parameter is persistent and allows for multiple render request to be 
	 * serviced while keeping information about the latest action's execution status.
	 */
	public static final String STRUTLETS_RESULT = "org.dihedron.strutlets.result";
	
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
