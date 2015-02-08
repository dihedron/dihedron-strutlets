/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.web.tomcat7x;

import org.dihedron.strutlets.containers.web.tomcat.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the Tomcat runtime environment.
 * 
 * @author Andrea Funto'
 */
public class Tomcat70x extends Tomcat {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Tomcat70x.class);
	
	/**
	 * Constructor has package visibility to prevent construction by anyone except 
	 * its plugin.
	 */
	Tomcat70x() {		
	}

	/**
	 * Returns the label of the Apache Tomcat Application Server.
	 * 
	 * @return
	 *   the label of the Apache Tomcat Application Server.
	 */
	@Override
	public String getName() {
		return "Apache Tomcat ver. 7.0.x";
	}
	
	/**
	 * Performs Tomcat-specific initialisation tasks.
	 * 
	 * @see org.dihedron.strutlets.containers.web.ApplicationServer#initialise()
	 */
	public boolean initialise() {
		logger.debug("initialising Tomcat 7.x runtime environment...");
		return true;
	}

	/**
	 * No Tomcat-specific cleanup tasks.
	 */
	@Override
	public void cleanup() {
	}
}
