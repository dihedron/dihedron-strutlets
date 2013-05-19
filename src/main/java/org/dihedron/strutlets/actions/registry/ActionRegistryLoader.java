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

package org.dihedron.strutlets.actions.registry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.Target;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.utils.Resource;
import org.dihedron.utils.Strings;
import org.dihedron.xml.DomHelper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The class responsible for loading the actions configuration ("targets"), either
 * from the XML file or from the classpath.
 * 
 * @author Andrea Funto'
 */
public class ActionRegistryLoader {

	/**
	 * The default name of the actions configuration file.
	 */
	public static final String DEFAULT_ACTIONS_CONFIG_XML = "actions-config.xml";		
	
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
	private static Logger logger = LoggerFactory.getLogger(ActionRegistryLoader.class);
	
	/**
	 * Whether the input XML file should be validated.
	 */
	private static final boolean VALIDATE_XML = false;
		
	/**
	 * The name of the default actions configuration schema file.
	 */
	private static final String ACTIONS_CONFIG_XSD = "org/dihedron/strutslet/actions/actions-config.xsd";
	
	/**
	 * Tries to initialise the actions' configuration by parsing the input 
	 * XML file, which must be available as a regular file on the file-system.
	 * 
	 * @param registry
	 *   the actions' metadata repository to be loaded.
	 * @param filepath
	 *   the path to the input configuration file on the file-system.
	 * @throws IOException 
	 * @throws StrutletsException 
	 */
	public void loadFromFileSystem(ActionRegistry registry, String filepath) throws IOException, StrutletsException  {
		InputStream stream = Resource.getAsStreamFromFileSystem(filepath);
		loadFromStream(registry, stream);
	}
	
	/**
	 * Attempts to initialise the actions' configuration by parsing the input 
	 * configuration file as read from the file-system.
	 * 
	 * @param registry
	 *   the actions' metadata repository to be loaded.
	 * @param file
	 *   the <code>File</code> object representing the configuration file on
	 *   the file-system.
	 * @throws IOException 
	 * @throws StrutletsException 
	 */
	public void loadFromFileSystem(ActionRegistry registry, File file) throws IOException, StrutletsException {
		if(file != null) { 
			InputStream stream = Resource.getAsStreamFromFileSystem(file);
			loadFromStream(registry, stream);
		}
	}
	
	/**
	 * tries to initialises the actions' configuration by parsing the input 
	 * configuration file as read from the classpath.
	 * 
	 * @param registry
	 *   the actions' metadata repository to be loaded.
	 * @param path
	 *   the path to the resource, to be located on the classpath.
	 * @throws StrutletsException
	 */
	public void loadFromClassPath(ActionRegistry registry, String path) throws StrutletsException {
		if(path !=null && path.length() > 0) {
			InputStream stream = Resource.getAsStreamFromClassPath(path);
			loadFromStream(registry, stream);
		}
	}

	/**
	 * Initialises the configuration by parsing the input stream; if the stream 
	 * is null, it returns immediately without any error, in order to support 
	 * automagical self-configuring <code>Actions</code>s, which do not require
	 * any configuration file.
	 * 
	 * @param registry
	 *   the actions' metadata repository to be loaded.
	 * @param stream
	 *   the configuration file as a stream; the stream will always be closed 
	 *   by the time the method returns. If null, the method returns immediately.
	 * @throws StrutletsException
	 */
	public void loadFromStream(ActionRegistry registry, InputStream input) throws StrutletsException {
				
		if(input == null) {
			logger.warn("no valid actions' configuration found, controller will rely on annotated actions");
			return;
		}
		
		InputStream stream = input;		
		InputStream xsd = null;
		try {
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(VALIDATE_XML);
			factory.setNamespaceAware(true);

			xsd = getClass().getClassLoader().getResourceAsStream(ACTIONS_CONFIG_XSD);
			if(xsd == null) {
				logger.warn("XSD for actions configuration not found");
			} else {
				logger.trace("XSD for actions configuration loaded");
				SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
				factory.setSchema(schemaFactory.newSchema(new Source[] {new StreamSource(xsd)}));
			}

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ConfigurationErrorHandler());
			
			Document document = builder.parse(stream);
			document.getDocumentElement().normalize();
			
			for(Element actions : DomHelper.getDescendantsByTagName(document, "actions")) {				
				
				String classpkg = DomHelper.getElementText(DomHelper.getFirstChildByTagName(actions, "package"));
				String interceptors = DomHelper.getElementText(DomHelper.getFirstChildByTagName(actions, "interceptors"));
				
				logger.debug("action package '{}' has default java package '{}' and interceptor stack '{}'", 
						actions.getAttribute("id"), classpkg, interceptors);				
								
				for(Element action : DomHelper.getChildrenByTagName(actions, "action")) {
					Target target = null;
					String actionid = action.getAttribute("id");					
					String classname = DomHelper.getElementText(DomHelper.getFirstChildByTagName(action, "class"));
										
					logger.debug(" + action '{}' is an instance of class '{}'", actionid, classname);
					
					Map<String, String> params = new HashMap<String, String>();
					Element parameters = DomHelper.getFirstChildByTagName(action, "parameters");
					if(parameters != null) {
						for(Element parameter : DomHelper.getChildrenByTagName(parameters, "parameter")) {
							String key = DomHelper.getElementText(DomHelper.getFirstChildByTagName(parameter, "key"));
							String value = DomHelper.getElementText(DomHelper.getFirstChildByTagName(parameter, "value"));
							params.put(key, value);
							logger.debug("   + parameter '{}' has value '{}'", key, value);
						}
					}					

					for(Element method : DomHelper.getChildrenByTagName(DomHelper.getFirstChildByTagName(action, "methods"), "method")) {						
						String methodid = method.getAttribute("id");

						target = new Target(actionid, methodid, false);
						target.setPackageName(classpkg);
						target.setInterceptorsStackId(interceptors);
						target.setClassName(classname);
						target.addParameters(params);																	
						target.setIdempotent(Boolean.parseBoolean(method.getAttribute("idempotent")));
						logger.debug("   + method {} idempotent", target.isIdempotent() ? "is" : "is not");
						
						logger.debug("   + method '{}' supports: ", methodid);						
						Element events = DomHelper.getFirstChildByTagName(method, "events");
						if(events != null) {
							for(Element event : DomHelper.getChildrenByTagName(events, "event")) {
								String namespace = event.getAttribute("namespace");
								String name = event.getTextContent();
								logger.debug("     + event event '{{}}{}'", namespace, name);
								registry.putEventTarget(new QName(namespace, name), target.getId());
							}
						}
						
						for(Element result : DomHelper.getChildrenByTagName(method, "result")) {
							String resultid = result.getAttribute("id");
							String renderer = result.getAttribute("renderer");
							String data = DomHelper.getElementText(result);
							WindowState state = WindowState.fromString(result.getAttribute("state"));
							PortletMode mode = PortletMode.fromString(result.getAttribute("mode"));							
							target.addResult(resultid, renderer, data, mode, state);
							logger.debug("     + result '{}' with renderer '{}' and data '{}' (mode: '{}', state: '{}')", resultid, renderer, data, mode, state);
						}
						registry.putTarget(target.getId(), target);
					}
				}
			}
			logger.info("configuration loaded");
		} catch (Exception e) {
			logger.error("error parsing input configuration", e);
			throw new StrutletsException("error parsing input configuration", e);
		} finally {
			if(stream != null) {
				try {
					stream.close();
					stream = null;
				} catch (IOException e) {
					throw new StrutletsException("error closing XML configuration stream", e);
				}				
			}
			if(xsd != null) {
				try {
					xsd.close();
					xsd = null;
				} catch (IOException e) {
					throw new StrutletsException("error closing XSD configuration stream", e);
				}
			}
		}
	}
	
    /**
     * This method performs the automatic scanning of dynamic actions at startup
     * time, to make access to actions faster later on. The targets map is
     * pre-populated with information coming from actions configured through
     * annotations and residing in the default java package, as per the user's 
     * configuration.
     * 
     * @throws StrutletsException
     */
    public void loadFromJavaPackage(ActionRegistry registry, String javaPackage) throws StrutletsException {
    	
    	if(Strings.isValid(javaPackage)) {
    		logger.trace("looking for action classes in package '{}'", javaPackage);

    		// use this approach because it seems to be consistently faster
    		// than the much simpler new Reflections(javaPackage) 
    		Reflections reflections = 
    				new Reflections(new ConfigurationBuilder()
    					.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(javaPackage)))
    					.setUrls(ClasspathHelper.forPackage(javaPackage))
    					.setScanners(new SubTypesScanner()));    		
    		Set<Class<? extends Action>> actions = reflections.getSubTypesOf(Action.class);
	        for(Class<?> clazz : actions) {
	        	logger.trace("action class: '{}'", clazz.getName());
	        	Class<?> iteratorClass = clazz;
	        	Set<Method> methods = new HashSet<Method>();
	        	while(iteratorClass != null && iteratorClass!= Object.class) { 
	        		Method[] set = iteratorClass.getDeclaredMethods();
	        		methods.addAll(Arrays.asList(set));
	        		iteratorClass = iteratorClass.getSuperclass();
	        	}
	        	for(Method method : methods) {	        		
	        		if(method.isAnnotationPresent(Invocable.class)) {
		        		logger.trace("checking annotated method '{}' in class '{}'", method.getName(), clazz.getSimpleName());
		        		registry.addTarget(clazz.getSimpleName(), method.getName());
	        		} else {
	        			logger.trace("discarding unannotated method '{}' in class '{}'", method.getName(), clazz.getSimpleName());
	        		}
	        	}
	        }
    	}
    }	
}
