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

import java.util.HashMap;

import javax.portlet.GenericPortlet;

import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class RendererRegistry extends HashMap<String, String> {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -4808717733886640936L;

	/**
	 * The Java package where the default set of renderers is located.
	 */
	public static final String DEFAULT_RENDERER_PACKAGE = "org.dihedron.strutlets.renderers.impl";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RendererRegistry.class);

	/**
	 * The portlet this registry belongs to.
	 */
	private GenericPortlet portlet;
	
	/**
	 * Constructor.
	 */
	public RendererRegistry(GenericPortlet portlet) {
		logger.info("instantiating renderers registry...");
		this.portlet = portlet;
		this.put("jsp", "org.dihedron.strutlets.renderers.impl.JspRenderer");
	}
	
	public Renderer makeRenderer(String type) throws StrutletsException {
		String classname = null;
		Renderer renderer = null;
		try {
			if(Strings.areValid(type, this.get(type))) {
				classname = this.get(type);
				@SuppressWarnings("unchecked")
				Class<? extends Renderer> clazz = (Class<? extends Renderer>) Class.forName(classname);
				renderer = clazz.newInstance();
				renderer.setPortlet(portlet);
			}
		} catch (ClassNotFoundException e) {
			logger.error("class '{}' not found on classpath", classname);
			throw new StrutletsException("Renderer class '" + classname + "' not found on classpath");
		} catch (InstantiationException e) {
			logger.error("error instantiating object of class '{}'", classname);
			throw new StrutletsException("Error instantiating renderer class '" + classname + "'");
		} catch (IllegalAccessException e) {
			logger.error("error accessing class '{}'", classname);
			throw new StrutletsException("Error accessing renderer class '" + classname + "'");
		}
		return renderer;
	}
}
