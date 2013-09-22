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

package org.dihedron.strutlets.renderers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.GenericPortlet;

import org.dihedron.commons.utils.Strings;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.strutlets.renderers.registry.RendererRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class CachingRendererRegistry implements RendererRegistry {
		
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(CachingRendererRegistry.class);

	/**
	 * The portlet this registry belongs to.
	 */
	private GenericPortlet portlet;
	
	/**
	 * The map containing an instance of each registered renderers.
	 */
	private Map<String, Renderer> renderers = new HashMap<String, Renderer>();  
	
	/**
	 * Constructor.
	 */
	public CachingRendererRegistry(GenericPortlet portlet) {
		logger.info("instantiating caching renderers registry...");
		this.portlet = portlet;
		//this.addRenderer("jsp", "org.dihedron.strutlets.renderers.impl.JspRenderer");
	}
	
	public void addRenderer(String id, Class<? extends Renderer> clazz) throws StrutletsException {
		if(Strings.isValid(id) && clazz != null) {
			try {
				logger.info("registering renderer '{}' of class '{}'", id, clazz.getName());
				Renderer renderer = clazz.newInstance();
				renderer.setPortlet(this.portlet);
				this.renderers.put(id, renderer);
			} catch (InstantiationException e) {
				logger.error("error instantiating object of class '{}'", clazz.getCanonicalName());
				throw new StrutletsException("Error instantiating renderer class '" + clazz.getCanonicalName() + "'", e);
			} catch (IllegalAccessException e) {
				logger.error("error accessing class '{}'", clazz.getCanonicalName());
				throw new StrutletsException("Error accessing renderer class '" + clazz.getCanonicalName() + "'", e);
			}				
		}
	}
	
	public Renderer getRenderer(String id) throws StrutletsException {
		Renderer renderer = null;
		if(Strings.isValid(id)) {
			renderer = this.renderers.get(id);
		}
		return renderer;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("renderers: [\n");
		for(Entry<String, Renderer> entry : renderers.entrySet()) {
			buffer.append("  { name: '").append(entry.getValue().getId()).append("', class: '").append(entry.getValue().getClass().getCanonicalName()).append("' },\n");
		}
		buffer.append("]\n");		
		return buffer.toString();
	}
}
