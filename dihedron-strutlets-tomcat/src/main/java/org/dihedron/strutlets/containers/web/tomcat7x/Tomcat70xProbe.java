/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.containers.web.tomcat7x;

import org.dihedron.strutlets.containers.web.tomcat.TomcatProbe;

/**
 * @author Andrea Funto'
 */
public class Tomcat70xProbe extends TomcatProbe {
	
	/**
	 * Constructor has package visibility to prevent construction by anyonw except 
	 * its plugin.
	 */
	Tomcat70xProbe() {		
	}

	/**
	 * @see org.dihedron.strutlets.containers.web.tomcat.TomcatProbe#getReferenceName()
	 */
	@Override
	protected String getReferenceName() {
		return "Apache Tomcat";
	}

	/**
	 * @see org.dihedron.strutlets.containers.web.tomcat.TomcatProbe#getReferenceVersion()
	 */
	@Override
	protected String getReferenceVersion() {
		return "7.0.";
	}
	
}
