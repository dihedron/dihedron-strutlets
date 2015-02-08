/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.interceptors;

import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * @author Andrea Funto'
 */
public class InterceptorStack extends ArrayList<Interceptor> {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 1022726179711950362L;
	
	/**
	 * The interceptor stack identifier.
	 */
	private String id;
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the interceptor stack identifier.
	 */
	public InterceptorStack(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the interceptor stack identifier.
	 * 
	 * @return
	 *   he interceptor stack identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Provides a pretty-printed string representation of the object.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("stack('").append(id).append("')");
		if(!this.isEmpty()) {
			buffer.append(" {\n");
			for(Interceptor interceptor : this ) {
				buffer.append("  interceptor('").append(interceptor.getId()).append("')");
				if(!interceptor.getParameters().isEmpty()) {
					buffer.append(" {\n");
					for(Entry<String, String> entry : interceptor.getParameters().entrySet()) {
						buffer.append("    parameter('").append(entry.getKey()).append("') = '").append(entry.getValue()).append("'\n");
					}
					buffer.append("  }");
				}
				buffer.append("\n");			
			}
			buffer.append("}");
		}
		buffer.append("\n");
		return buffer.toString();
	}
}
