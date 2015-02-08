/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.renderers.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.GenericPortlet;

import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.renderers.Renderer;
import org.dihedron.strutlets.renderers.registry.RendererRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class RenewingRendererRegistry implements RendererRegistry {
		
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RenewingRendererRegistry.class);

	/**
	 * The portlet this registry belongs to.
	 */
	private GenericPortlet portlet;
	
	/**
	 * The map containing the classes of all registered renderers.
	 */
	private Map<String, Class<? extends Renderer>> renderers = new HashMap<String, Class<? extends Renderer>>();  
	
	/**
	 * Constructor.
	 */
	public RenewingRendererRegistry(GenericPortlet portlet) {
		logger.info("instantiating renewing renderers registry...");
		this.portlet = portlet;
	}
	
	public void addRenderer(String id, Class<? extends Renderer> clazz) {
		if(Strings.isValid(id) && clazz != null) {
			logger.info("registering renderer '{}' of class '{}'", id, clazz.getName());
			this.renderers.put(id, clazz);
		}
	}
	
	public Renderer getRenderer(String id) throws StrutletsException {
		String classname = null;
		Renderer renderer = null;
		try {
			if(Strings.isValid(id)) {
				Class<? extends Renderer> clazz = this.renderers.get(id);
				renderer = clazz.newInstance();
				renderer.setPortlet(portlet);
			}
		} catch (InstantiationException e) {
			logger.error("error instantiating object of class '{}'", classname);
			throw new StrutletsException("Error instantiating renderer class '" + classname + "'", e);
		} catch (IllegalAccessException e) {
			logger.error("error accessing class '{}'", classname);
			throw new StrutletsException("Error accessing renderer class '" + classname + "'", e);
		}
		return renderer;
	}
	
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("renderers: [\n");
		for(Entry<String, Class<? extends Renderer>> entry : renderers.entrySet()) {
			buffer.append("  name: '").append(entry.getKey()).append("', class: '").append(entry.getValue().getCanonicalName()).append("' },\n");
		}
		buffer.append("]\n");		
		return buffer.toString();
	}
	
}
