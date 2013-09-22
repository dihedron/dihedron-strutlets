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
package org.dihedron.commons.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * A {@link URLStreamHandler} that handles resources on the classpath.
 * 
 * @see
 *   http://stackoverflow.com/questions/861500/url-to-load-resources-from-the-classpath-in-java
 */
public class ClassPathURLStreamHandler extends URLStreamHandler {
	
    /** 
     * The class loader to find resources from. 
     */
    private final ClassLoader classloader;
    
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ClassPathURLStreamHandler.class);

    /**
     * Constructor.
     */
    public ClassPathURLStreamHandler() {
        this.classloader = getClass().getClassLoader();
    }

    /**
     * Constructor.
     *
     * @param classloader
     *   a user-defined class loader.
     */
    public ClassPathURLStreamHandler(ClassLoader classloader) {
        this.classloader = classloader;
    }

    /**
     * Opens a connection to the given resource, if found in the classpath.
     * 
     * @param url
     *   the URL representing the path to the resource, in the following 
     *   format: <code>classpath:path/to/resource.ext</code>.
     * @return
     *   the <code>URLConnection/code> to the requested resource.
     */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
    	logger.trace("opening connection to resource '{}' (protocol: '{}'", url.getPath(), url.getProtocol());
        URL resource = classloader.getResource(url.getPath());
        return resource.openConnection();
    }
}
