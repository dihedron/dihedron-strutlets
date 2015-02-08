/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.demo.portlets.portlet9.actions;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A simple output stream that discards everything that's written to it.
 * 
 * @author Andrea Funto'
 */
public class SinkOutputStream extends OutputStream {
	
	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
	}
}
