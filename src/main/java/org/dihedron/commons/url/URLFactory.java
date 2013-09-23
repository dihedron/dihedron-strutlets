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

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class to create custo URLs, associating them with their respective
 * stream handlers; this allows to create URLs that refer to custom 
 * protocols such as <code>classpath:</code>.
 * 
 * @author Andrea Funto'
 */
public class URLFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(URLFactory.class);

	/**
	 * Returns an URL object for the given URL specification.
	 * 
	 * @param specification
	 *   the URL specification.
	 * @return
	 *   an URL object; if the URL is of "classpath://" type, it will return an URL
	 *   whose connection will be opened by a specialised stream handler. 
	 * @throws MalformedURLException
	 */
	public static URL makeURL(String specification) throws MalformedURLException {
		logger.trace("retrieving URL for specification: '{}'", specification);
		if(specification.startsWith("classpath:")) {
			logger.trace("URL is of type 'classpath'");
			return new URL(null, specification, new ClassPathURLStreamHandler());
		} 
		logger.trace("URL is of normal type");
		return new URL(specification);
	}
	
	/**
	 * Private constructor, to prevent instantiation.
	 */
	private URLFactory() {
	}
}
