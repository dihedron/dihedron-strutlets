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

package org.dihedron.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public final class Resource {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Resource.class);
	
	/**
	 * Returns the given resource as a a stream: if the resource is a file
	 * on the file-system, it is opened there; if not found on the file-system,
	 * it is looked for on the classpath. 
	 * NOTE: The returned stream MUST be closed by the caller, to avoid resource
	 * leakage. 
	 * 
	 * @param path
	 *   the path to the resource.
	 * @return
	 *   an open stream if the resource could be found; null otherwise.
	 * @throws IOException 
	 */
	public static InputStream getAsStream(String path) throws IOException {
		
		InputStream stream = getAsStreamFromFileSystem(path);
		if(stream == null) {
			stream = getAsStreamFromClassPath(path);
		}
		return stream;
	}

	/**
	 * Opens a resource on the file-system as a stream.
	 * 
	 * @param path
	 *   the path of the resource, as a string.
	 * @return
	 *   an open stream, or null if not found. The stream MUST be closed by the
	 *   caller to avoid resource leaks.
	 * @throws IOException 
	 */
	public static InputStream getAsStreamFromFileSystem(String path) throws IOException {		
		return getAsStreamFromFileSystem(new File(path));
	}
	
	/**
	 * Opens a resource on the file-system as a stream.
	 * 
	 * @param file
	 *   the <code>File</code> object representing the resource.
	 * @return
	 *   an open stream, or null if not found. The stream MUST be closed by the
	 *   caller to avoid resource leaks.
	 * @throws IOException 
	 */
	public static InputStream getAsStreamFromFileSystem(File file) throws IOException {
		
		// input file, if available, is preferable
		if(file != null && file.exists() && file.isFile()) {
			logger.trace("resource '{}' is a regular file", file.getCanonicalPath());
			return new FileInputStream(file);
		} 
		logger.trace("resource '{}' is not a regular file", file.getCanonicalPath());
		return null;					
	}	
	
	/**
	 * Opens a resource in the class path as a stream.
	 * 
	 * @param path
	 *   the path of the resource; as an example, to get a stream to resource 
	 *   <code>org.dihedron.Example</code>, use path "org/dihedron/Example", with
	 *   no leading slash.
	 * @return
	 *   an open stream, or null if not found. The stream MUST be closed by the 
	 *   caller to avoid resource leaks.
	 */
	public static InputStream getAsStreamFromClassPath(String path) {
		// this is not on the file-system, let's try to get it from the class loader
		InputStream stream = Resource.class.getClassLoader().getResourceAsStream(path);
		if(stream != null) {
			logger.trace("resource '{}' is on the classpath", path);
		} else {
			logger.trace("resource '{}' is not on the classpath", path);
		}
		return stream;		
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private Resource() {
	}	

}
