/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.containers.web.tomcat;

import org.apache.catalina.util.ServerInfo;
import org.dihedron.strutlets.containers.web.ApplicationServer;

/**
 * A class representing the Tomcat runtime environment.
 * 
 * @author Andrea Funto'
 */
public abstract class Tomcat implements ApplicationServer {
	
	/**
	 * Returns a string describing the Tomcat application server name and version.
	 * 
	 * @return
	 *   a string describing the Tomcat application server name and version.
	 */
	public String getServerInfo() {
		return ServerInfo.getServerInfo();
	}
	
	/**
	 * Returns a string describing the Tomcat application server version.
	 * 
	 * @return
	 *   a string describing the Tomcat application server version.
	 */
	public String getServerVersion() {
		return ServerInfo.getServerNumber();
	}
}
