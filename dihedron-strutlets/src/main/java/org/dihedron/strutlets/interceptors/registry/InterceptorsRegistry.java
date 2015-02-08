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

package org.dihedron.strutlets.interceptors.registry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dihedron.core.url.URLFactory;
import org.dihedron.core.xml.DOM;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.interceptors.Interceptor;
import org.dihedron.strutlets.interceptors.InterceptorStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Andrea Funto'
 */
public class InterceptorsRegistry {
	
	public class ConfigurationErrorHandler implements ErrorHandler {
	    public void warning(SAXParseException e) throws SAXException {
	        logger.warn(e.getMessage(), e);
	    }

	    public void error(SAXParseException e) throws SAXException {
	        logger.error(e.getMessage(), e);
	    }

	    public void fatalError(SAXParseException e) throws SAXException {
	        logger.error(e.getMessage(), e);
	    }
	}	

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(InterceptorsRegistry.class);
	
	/**
	 * Whether the input XML file should be validated.
	 */
	private static final boolean VALIDATE_XML = false;
	
	/**
	 * The name of the interceptors configuration schema file.
	 */
	public static final String INTERCEPTORS_CONFIG_XSD = "classpath:org/dihedron/strutlets/interceptors/interceptors.xsd";
	
	/**
	 * The name of the file declaring the default interceptor stack.
	 */
	public static final String DEFAULT_INTERCEPTORS_CONFIG_XML = "classpath:org/dihedron/strutlets/default-interceptors.xml";

	/**
	 * The name of the default interceptor stack ("default").
	 */
	public static final String DEFAULT_INTERCEPTOR_STACK = "default";
	
	/**
	 * The map of registered interceptor stacks.
	 */
	private Map<String, InterceptorStack> stacks = Collections.synchronizedMap(new HashMap<String, InterceptorStack>());
	
//	/**
//	 * Initialises the configuration by parsing the input configuration file
//	 * as read from the file-system.
//	 * 
//	 * @param filepath
//	 *   the path to the input configuration file on the file-system.
//	 * @throws IOException 
//	 * @throws StrutletsException 
//	 * @throws Exception
//	 */
//	public void loadFromFileSystem(String filepath) throws IOException, StrutletsException {
//		InputStream stream = Resource.getAsStreamFromFileSystem(filepath);
//		loadFromStream(stream);
//	}
//	
//	/**
//	 * Initialises the configuration by parsing the input configuration file
//	 * as read from the file-system.
//	 * 
//	 * @param file
//	 *   the <code>File</code> object representing the configuration file on
//	 *   the file-system.
//	 * @throws IOException 
//	 * @throws Exception
//	 */
//	public void loadFromFileSystem(File file) throws StrutletsException, IOException {
//		InputStream stream = Resource.getAsStreamFromFileSystem(file);
//		loadFromStream(stream);
//	}
//	
//	/**
//	 * Initialises the configuration by parsing the input configuration file
//	 * as read from the classpath.
//	 * 
//	 * @param path
//	 *   the path to the resource, to be located on the classpath.
//	 * @throws StrutletsException
//	 */
//	public void loadFromClassPath(String path) throws StrutletsException {
//		InputStream stream = Resource.getAsStreamFromClassPath(path);
//		loadFromStream(stream);
//	}
	
	/**
	 * Initialises the configuration by parsing the input configuration file
	 * as read from the given URL; URLs are interpreted using {@link URLFactory},
	 * so they can include classpath resources. If the XML input stream is not 
	 * valid, it returns without complaining in order to make interceptors registry 
	 * configuration optional.
	 * 
	 * @param specification
	 *   a string representing the XML resource to be loaded; it can be any web
	 *   URL, or the path of a resource on the classpath.
	 * @throws StrutletsException
	 *   if the resource cannot be located or read.  
	 * @see URLFactory
	 */
	public void load(String specification) throws StrutletsException {
				
		try {
			
			logger.info("loading the interceptors configuration from '{}'", specification);
			
			// interpret the specification as an URL
			URL url =  URLFactory.makeURL(specification);		
			
			// get the URL to the intercetpros configuration XSD
			URL xsd = URLFactory.makeURL(INTERCEPTORS_CONFIG_XSD);		
			try(InputStream xmlStream = url.openStream(); InputStream xsdStream = xsd.openStream()) {
	
				if(xmlStream == null) {
					logger.warn("error opening the interceptors configuration stream from '{}'", specification);
					return;
				}
	
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//			factory.setValidating(VALIDATE_XML);
				factory.setIgnoringElementContentWhitespace(true);
				factory.setNamespaceAware(true);
				
				if(xsdStream == null) {
					logger.warn("error opening the XSD stream for interceptors configuration from '{}'", INTERCEPTORS_CONFIG_XSD);
				} else {
					logger.trace("XSD for interceptors configuration loaded");
					SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
					Schema schema = schemaFactory.newSchema(new StreamSource(xsdStream));
					factory.setSchema(schema);
				}
	
				DocumentBuilder builder = factory.newDocumentBuilder();
				builder.setErrorHandler(new ConfigurationErrorHandler());
				
				Document document = builder.parse(xmlStream);
				document.getDocumentElement().normalize();
				
				for(Element s : DOM.getDescendantsByTagName(document, "stack")) {
					
					String stackId = s.getAttribute("id");
					InterceptorStack stack = new InterceptorStack(stackId);
					logger.trace("interceptor stack '{}' ", stack.getId());
					
					for(Element i : DOM.getChildrenByTagName(s, "interceptor")) {
						String interceptorId = i.getAttribute("id");
						String interceptorClass = i.getAttribute("class");
						Interceptor interceptor = (Interceptor)Class.forName(interceptorClass).newInstance();
						interceptor.setId(stackId, interceptorId);
						logger.trace(" + interceptor '{}' ", interceptorId);
						
						for(Element parameter : DOM.getChildrenByTagName(i, "parameter")) {
							String key = DOM.getElementText(DOM.getFirstChildByTagName(parameter, "key"));
							String value = DOM.getElementText(DOM.getFirstChildByTagName(parameter, "value"));
							interceptor.setParameter(key, value);
							logger.trace("   + parameter '{}' has value '{}'", key, value);
						}	
						
						interceptor.initialise();
						
						stack.add(interceptor);
					}
					stacks.put(stack.getId(), stack);
				}
				logger.info("configuration loaded");
			}
				
		} catch (IOException | SAXException | InstantiationException | IllegalAccessException | ClassNotFoundException | ParserConfigurationException e) {
			logger.error("error parsing interceptors rgistry configuration", e);
			throw new StrutletsException("error parsing interceptors registry configuration", e);
		}
	}
	
//	/**
//	 * Initialises the configuration by parsing the input configuration file,
//	 * passed in as an input stream.
//	 * 
//	 * @param input
//	 *   the configuration file as a stream; the stream will always be closed 
//	 *   by the time the method returns. If the stream is null, the method
//	 *   exits immediately without any complaint, in order to make interceptors'
//	 *   loading optional.
//	 * @throws StrutletsException
//	 */
//	public void loadFromStream(InputStream input) throws StrutletsException {
//		
//		if(input == null) {
//			logger.warn("invalid input stream");
//			return;
//		}
//		InputStream stream = input; 
//		
//		InputStream xsd = null;
//		try {
//		
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
////			factory.setValidating(VALIDATE_XML);
//			factory.setIgnoringElementContentWhitespace(true);
//			factory.setNamespaceAware(true);
//
//			xsd = Resource.getAsStreamFromClassPath(INTERCEPTORS_CONFIG_XSD);
//			if(xsd == null) {
//				logger.warn("error loading XSD for interceptors configuration");
//			} else {
//				logger.trace("XSD for interceptors configuration loaded");
//				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//				Schema schema = schemaFactory.newSchema(new StreamSource(xsd));
//				factory.setSchema(schema);
//			}
//
//			DocumentBuilder builder = factory.newDocumentBuilder();
//			builder.setErrorHandler(new ConfigurationErrorHandler());
//			
//			Document document = builder.parse(stream);
//			document.getDocumentElement().normalize();
//			
//			for(Element s : DomHelper.getDescendantsByTagName(document, "stack")) {
//				
//				String stackId = s.getAttribute("id");
//				InterceptorStack stack = new InterceptorStack(stackId);
//				logger.trace("interceptor stack '{}' ", stack.getId());
//				
//				for(Element i : DomHelper.getChildrenByTagName(s, "interceptor")) {
//					String interceptorId = i.getAttribute("id");
//					String interceptorClass = i.getAttribute("class");
//					Interceptor interceptor = (Interceptor)Class.forName(interceptorClass).newInstance();
//					interceptor.setId(stackId, interceptorId);
//					logger.trace(" + interceptor '{}' ", interceptorId);
//					
//					for(Element parameter : DomHelper.getChildrenByTagName(i, "parameter")) {
//						String key = DomHelper.getElementText(DomHelper.getFirstChildByTagName(parameter, "key"));
//						String value = DomHelper.getElementText(DomHelper.getFirstChildByTagName(parameter, "value"));
//						interceptor.setParameter(key, value);
//						logger.trace("   + parameter '{}' has value '{}'", key, value);
//					}	
//					
//					interceptor.initialise();
//					
//					stack.add(interceptor);
//				}
//				stacks.put(stack.getId(), stack);
//			}
//			logger.info("configuration loaded");
//		} catch (Exception e) {
//			logger.error("error parsing input configuration", e);
//			throw new StrutletsException("error parsing input configuration", e);
//		} finally {
//			if(stream != null) {
//				try {
//					stream.close();
//					stream = null;
//				} catch(IOException e) {
//					throw new StrutletsException("error closing XML configuration stream", e);
//				}
//			}
//			if(xsd != null) {
//				try {
//					xsd.close();
//					xsd = null;
//				} catch(IOException e) {
//					throw new StrutletsException("error closing XSD configuration stream", e);
//				}				
//			}
//		}
//	}
	
	/**
	 * Retrieves the stack corresponding to the given id.
	 *  
	 * @param id
	 *   the id of the stack to be retrieved.
	 * @return
	 *   the stack, or null if none found.
	 */
	public InterceptorStack getStack(String id) {
		return stacks.get(id);
	}
		
	/**
	 * Retrieves the stack corresponding to the given id.
	 *  
	 * @param id
	 *   the id of the stack to be retrieved.
	 * @return
	 *   the stack, or the default stack if not found.
	 */
	public InterceptorStack getStackOrDefault(String id) {
		InterceptorStack stack = stacks.get(id);
		if(stack == null) {
			stack = stacks.get(DEFAULT_INTERCEPTOR_STACK);
		}
		return stack;
	}
	
	/**
	 * Returns a pretty printed, complex representation of the object as a string.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(Entry<String, InterceptorStack> entry : stacks.entrySet()) {
			buffer.append("--------- INTERCEPTOR ---------\n");
			buffer.append(entry.getValue().toString());
		}
		buffer.append("-------------------------------\n");
		return buffer.toString();
	}
}
