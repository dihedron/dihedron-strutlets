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
		buffer.append("stack\n");
		buffer.append("+ id           : '").append(id).append("'\n");
		buffer.append("+ interceptors\n");
		for(Interceptor interceptor : this ) {
			buffer.append(" + interceptor\n");
			buffer.append("  + id         : '").append(interceptor.getId()).append("'\n");
			for(Entry<String, String> entry : interceptor.getParameters().entrySet()) {
				buffer.append("  + parameter\n");
				buffer.append("   + key       : '").append(entry.getKey()).append("'\n");
				buffer.append("   + value     : '").append(entry.getValue()).append("'\n");
			}
		}
		return buffer.toString();
	}
	
}
