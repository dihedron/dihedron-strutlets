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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.portlet.ActionRequest;

/**
 * A class to store general information about the library.
 * 
 * @author Andrea Funto'
 */
public final class Strutlets {
	
	/**
	 * The name of the system property through which the strutlets framework 
	 * upload directory can be specified.
	 */
	public static final String STRUTLETS_UPLOAD_DIR = "strutlets.upload.dir";
		
	/**
	 * A map containing the library properties, partially populated by the build
	 * process using information in the propject's POM (e.g. the library version).
	 */
	private static final Properties properties = new Properties();
	
	/**
	 * Initialises the library properties.
	 */
	static {
		InputStream stream = null;
		try {
			stream = Strutlets.class.getClassLoader().getResourceAsStream("strutlets.properties");
			properties.load(stream);
		} catch(IOException e) {
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {					
				}
			}
		}
	}
	
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
	 * The name of the render parameter under which the last-resort error JSP will
	 * be stored; this mechanism enables an error handler for the Action phase to
	 * prepare any information needed by this JSP to show an error message to the 
	 * user an then pass over the rendering responsibility to the JSP without 
	 * further intervention by the framework. 
	 */
	public static final String STRUTLETS_ERROR_JSP = "org.dihedron.strutlets.errorjsp";
	
	/**
	 * The name of the request scope attribute under which exception information
	 * is made available to the error JSP.
	 */
	public static final String STRUTLETS_ERROR_INFO = "org.dihedron.strutlets.errorinfo";
	
	/**
	 * Returns the framework's version (as per the project's POM).
	 * 
	 * @return
	 *   the framework's version (as per the project's POM).
	 */
	public static final String getVersion() {
		return properties.getProperty("strutlets.version");
	}
	
	/**
	 * Returns the Strutlets framework's web site.
	 * 
	 * @return
	 *   the Strutlets framework web site.
	 */
	public static final String getWebSite() {
		return properties.getProperty("strutlets.website");
	}
	
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
    	out.println(String.format("   |      STRUTLETS ver. %1$-8s   |", Strutlets.getVersion()));
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
