/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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
		try(InputStream stream = Strutlets.class.getClassLoader().getResourceAsStream("strutlets.properties")) {
			properties.load(stream);
		} catch(IOException e) {
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
	public static final String STRUTLETS_ERROR_JSP = "org.dihedron.strutlets.error-jsp";
		
	/**
	 * The name of the request scope attribute under which exception information
	 * is made available to the error JSP.
	 */
	public static final String STRUTLETS_ERROR_INFO = "org.dihedron.strutlets.error-info";
	
	public static final String STRUTLETS_FORM_TIMESTAMP = "formDate";
	
	public static final String STRUTLETS_LAST_FORM_TIMESTAMP = "org.dihedron.strutlets.last-form-timestamp";
	
	public static final String STRUTLETS_LAST_FORM_RESULT = "org.dihedron.strutlets.last-form-result";
	
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
