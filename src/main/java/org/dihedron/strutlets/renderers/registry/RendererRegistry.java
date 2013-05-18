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

package org.dihedron.strutlets.renderers.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dihedron.strutlets.actions.Target;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class RendererRegistry {
	
	/**
	 * The Java package where the default set of renderers is located.
	 */
	public static final String DEFAULT_RENDERER_PACKAGE = "org.dihedron.strutlets.renderers.impl";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RendererRegistry.class);

	/**
	 * The registry of renderers.
	 */
	private Map<String, Renderer> store = Collections.synchronizedMap(new HashMap<String, Renderer>());
	
//	/**
//	 * The package in which the custom renderers might be available.
//	 */
//	private String rendererPackage;
	
	/**
	 * Constructor.
	 */
	public RendererRegistry() {
		logger.info("instantiating renderers registry...");
	}
	
//	/**
//	 * Sets the value of the Java package to be used for custom (user-provided)
//	 * renderers. 
//	 * 
//	 * @param rendererPackage
//	 *   the Java package to be used for <code>Renderer</code>s.
//	 */
//	public void setCustomRendererPackage(String rendererPackage) {
//		if(Strings.isValid(rendererPackage)) {
//			this.rendererPackage = rendererPackage;
//			logger.info("java package for custom renderers: '{}'", this.rendererPackage);
//		}
//	}	
}
