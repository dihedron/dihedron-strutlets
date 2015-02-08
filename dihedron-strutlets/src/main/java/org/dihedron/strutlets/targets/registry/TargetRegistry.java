/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.targets.registry;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;

import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Event;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.targets.Target;
import org.dihedron.strutlets.targets.TargetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class containing the set of information available for the pool of supported 
 * actions; the information is stored in here by loading it from the configuration
 * file or by detecting an action's properties at runtime. This information can 
 * be used to instantiate new <code>AbstractAction</code>s and to get the output renderers 
 * for their results. 
 * 
 * @author Andrea Funto'
 */
public class TargetRegistry implements Serializable {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -1904121214568348128L;
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(TargetRegistry.class);
	/**
	 * The default root directory for JSP URLs.
	 */
	public static final String DEFAULT_HTML_ROOT_DIR = "/";
	
	/**
	 * The default pattern for JSP paths generation, for auto-configured targets.
	 */
	public static final String DEFAULT_HTML_PATH_PATTERN = "${rootdir}/${action}/${method}/${result}.jsp";

	/**
	 * The actual set of actions' targets. 
	 */
	private Map<TargetId, Target> store = new HashMap<TargetId, Target>(); 
	
	/**
	 * The set of event-to-target mappings.
	 */
	private Map<String, TargetId> events = new HashMap<String, TargetId>();
			
	/**
	 * The root directory to be used for <code>AbstractAction</code>s auto-configured 
	 * result URLs.
	 */
	private volatile String rootdir = DEFAULT_HTML_ROOT_DIR;
	
	/**
	 * The pattern to be used for conjuring up JSP pages for auto-configured 
	 * results. For a thorough discussion on format and accepted variables
	 * check out 
	 * {@link org.dihedron.strutlets.ActionController.InitParameter.RENDER_PATH_PATTERN InitParameter.RENDER_PATH_PATTERN}.
	 */	
	private volatile String pattern = DEFAULT_HTML_PATH_PATTERN;
		
	/**
	 * Constructor.
	 */
	public TargetRegistry() {
		logger.info("creating target repository");
	}
	
	/**
	 * Sets the information used by the framework to self-configure when there 
	 * is no explicit URL associated with a JSP-rendered result. This information
	 * is made up of two components:<ol>
	 * <li> the HTML package (directory) to be used as the base directory
	 * for self-configuring <code>AbstractAction</code>s lacking result URLs,</li>
	 * <li>the pattern used to create JSP URLs.; if not specified, URLs will be 
	 * conjured up according to the the following pattern: 
	 * &lt;root directory&gt;/&lt;action name&gt;/&lt;method name&gt;/&lt;result&gt;.jsp
	 * </ol>
	 * 
	 * @param rootdir
	 *   the root directory to be used as the starting point for fabricated URLs.
	 * @param pattern
	 *   the pattern to be used for locating auto-configured actions' JSPs.
	 */
	public void setHtmlPathInfo(String rootdir, String pattern) {		
		if(Strings.isValid(rootdir)) {
			logger.debug("HTML root directory from configuration: '{}'", rootdir);
			this.rootdir = rootdir;
			if(this.rootdir.endsWith("/")) {
				int index = this.rootdir.lastIndexOf('/');
				this.rootdir = this.rootdir.substring(0, index);
			}			
		}
		if(Strings.isValid(pattern)) {
			logger.debug("HTML URL pattern from configuration: '{}'", pattern);
			this.pattern = pattern;
		}
		
		this.pattern = this.pattern.replaceAll("\\$\\{rootdir\\}", this.rootdir);
		
		logger.info("root directory for conjured-up result URLs: '{}'", this.rootdir);
		logger.info("pattern for auto-configured targets' JSPs: '{}'", this.pattern);
	}
	
	/**
	 * Registers a new target (as a {@code TargetId}, {@code Target} pair)
	 * into the registry.
	 * 
	 * @param actionClass
	 *   the class of the action implementing the target's business logic.
	 * @param factoryMethod
	 *   the factory method that will instantiate the action using synthetic code
	 *   instead of reflection.
	 * @param actionMethod
	 *   the method implementing the target's business logic.
	 * @param invocable
	 *   the method annotation, from which some information might be extracted.
	 * @param interceptors
	 *   the name of the interceptor stack to be used for the given action.
	 * @throws StrutletsException 
	 */
	public void addTarget(Class<?> actionClass, Method factoryMethod, Method actionMethod, Method proxyMethod, 
			Invocable invocable, String interceptors) throws StrutletsException {
		String actionName = Strings.isValid(actionClass.getAnnotation(Action.class).alias()) ? actionClass.getAnnotation(Action.class).alias() : actionClass.getSimpleName(); 
		logger.info("adding target '{}!{}' (proxy: '{}')", actionName, actionMethod.getName(), proxyMethod.getName());
		TargetId id = new TargetId(actionClass, actionMethod);
		
		// instantiate the information object
		Target data = new Target(id);
		data.setActionClass(actionClass);
		data.setFactoryMethod(factoryMethod);
		data.setActionMethod(actionMethod);
		data.setProxyMethod(proxyMethod);
		data.setIdempotent(invocable.idempotent());
		data.setCacheable(invocable.cacheable());
		data.setInterceptorsStackId(interceptors);
		data.setJspUrlPattern(pattern);
		data.addDeclaredResults(invocable);
					
		logger.trace("auto-configuring events of '{}'", id);
		for(Event event : invocable.events()) {
			String name = event.value();
			String namespace = event.namespace();
			QName qname = new QName(namespace, name);
			events.put(qname.toString(), id);
		}		
		this.store.put(id,  data);
	}
	
	/**
	 * Retrieves the {@code Target} object corresponding to the given target
	 * identifier.
	 * 
	 * @param id
	 *   the target identifier.
	 * @return
	 *   the {@code Target} object; if none found in the registry, an
	 *   exception is thrown.
	 * @throws StrutletsException
	 *   if no @{code Target} object could be found for the given id.
	 */
	public Target getTarget(TargetId id) throws StrutletsException {
		if(!store.containsKey(id)) {
			logger.debug("repository does not contain info for target '{}'", id);
			throw new StrutletsException("Invalid target : '" + id.toString() + "'");
		}
		return store.get(id);		
	}
	
	/**
	 * Returns the @{code Target} corresponding to the action and method,
	 * as expressed in the given target string.
	 *  
	 * @param target
	 *   a string representing the action, with or without the method being invoked
	 *   on it; thus it can be the action name (in which case the default method 
	 *   "execute" is assumed) or the full target ("MyAction!myMethod").
	 * @return
	 *   the @{code Target} object corresponding to the given combination 
	 *   of action name and method name, if found. If no @{code Target} can 
	 *   be found an exception is thrown.
	 * @throws StrutletsException 
	 *   if the string is not a valid target or no target can be found corresponding 
	 *   to it.
	 */
	public Target getTarget(String target) throws StrutletsException {		 
		return getTarget(new TargetId(target));
	}

	/**
	 * Returns the @{code Target} corresponding to the action and method,
	 * as expressed in the given target string.
	 *  
	 * @param action
	 *   a string representing the action (e.g. "MyAction").
	 * @param method
	 *   a string representing the method (e.g. "myMethod").
	 * @return
	 *   the @{code Target} object corresponding to the given combination 
	 *   of action name and method name, if found. If no @{code Target} can 
	 *   be found an exception is thrown.
	 * @throws StrutletsException 
	 *   if the string is not a valid target or no target can be found corresponding 
	 *   to it.
	 */
	public Target getTarget(String action, String method) throws StrutletsException {			
		return getTarget(new TargetId(action, method));
	}
	
	/**
	 * Returns the set of all registered targets.
	 * 
	 * @return
	 *   the set of all registered targets.
	 */
	public Set<TargetId> getTargetIds() {
		return store.keySet();
	}
		
	/**
	 * Retrieves the identifier of the target that is able to support the given 
	 * event, if any.
	 * 
	 * @param qname
	 *   the <code>QName</code> of the event.
	 * @return
	 *   the id of the target able to process the given event.
	 */
	public TargetId getEventTarget(QName qname) {
		String key = qname.toString();
		logger.trace("looking up target for '{}'", key);
		return events.get(key);
	}
	
	/**
	 * Stores the name of the target that will handle events of the given type.
	 * Each action method can declare its supported events, and if so its name
	 * is stored in a separate lookup map that helps identify the action that will 
	 * handle a given event when it arrives. The lookup sequence is:<ol>
	 * <li>lookup the name of the target for the given event <code>QName</code>;</li>
	 * <li>lookup the <code>Target</code> for the given tagert name.</li>
	 * </ol>. Thus, event targets lookup takes one step more than simple action 
	 * processing lookups, because events must not be linked one-to-one to event
	 * names: the one-to-many relationship is guaranteed by each target storing 
	 * the set of events it is ready to handle in a separate map.
	 * 
	 * @param qname
	 *   the <code>QName</code> of the event.
	 * @param target
	 *   the id of the target (action/method) that will handle to the given 
	 *   request.
	 */
	public void addEventTarget(QName qname, TargetId target) {
		if(qname != null) {
			logger.trace("target '{}' will be invoked on events of type '{}'", target, qname.toString()); 
			events.put(qname.toString(),  target);
		}		
	}
	
	/**
	 * Stores the name of the target that will handle events of the given type.
	 * Each action method can declare its supported events, and if so its name
	 * is stored in a separate lookup map that helps identify the action that will 
	 * handle a given event when it arrives. The lookup sequence is:<ol>
	 * <li>lookup the name of the target for the given event <code>QName</code>;</li>
	 * <li>lookup the <code>Target</code> for the given tagert name.</li>
	 * </ol>. Thus, event targets lookup takes one step more than simple action 
	 * processing lookups, because events must not be linked one-to-one to event
	 * names: the one-to-many relationship is guaranteed by each target storing 
	 * the set of events it is ready to handle in a separate map.
	 * 
	 * @param qname
	 *   the <code>QName</code> of the event.
	 * @param target
	 *   the name of the target (action/method) that will handle to the given 
	 *   request.
	 * @throws StrutletsException 
	 *   if the name of the target is not a valid name.
	 */
	public void addEventTarget(QName qname, String target) throws StrutletsException {
		if(qname != null) {
			logger.trace("target '{}' will be invoked on events of type '{}'", target, qname.toString()); 
			events.put(qname.toString(), new TargetId(target));
		}		
	}
	
	/**
	 * Stores the name of the target that will handle events of the given type.
	 * Each action method can declare its supported events, and if so its name
	 * is stored in a separate lookup map that helps identify the action that will 
	 * handle a given event when it arrives. The lookup sequence is:<ol>
	 * <li>lookup the name of the target for the given event <code>QName</code>;</li>
	 * <li>lookup the <code>Target</code> for the given tagert name.</li>
	 * </ol>. Thus, event targets lookup takes one step more than simple action 
	 * processing lookups, because events must not be linked one-to-one to event
	 * names: the one-to-many relationship is guaranteed by each target storing 
	 * the set of events it is ready to handle in a separate map.
	 * 
	 * @param qname
	 *   the <code>QName</code> of the event.
	 * @param actionName
	 *   the name of the action class that contains the method that will handle 
	 *   the given request.
	 * @param methodName
	 *   the name of the method that will handle the given request.
	 * @throws StrutletsException 
	 *   if the action/method combination does not make a valid target.
	 */
	public void addEventTarget(QName qname, String actionName, String methodName) throws StrutletsException {
		if(qname != null) {
			logger.trace("method '{}' of action '{}' will be invoked on events of type '{}'", methodName, actionName, qname.toString()); 
			events.put(qname.toString(),  new TargetId(actionName, methodName));
		}		
	}

	/**
	 * Provides a string representation of the registry.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("");
		
		if(!store.isEmpty()) {
			for(Entry<TargetId, Target> entry : store.entrySet()) {
				buffer.append("----------- TARGET -----------\n");
				buffer.append(entry.getValue().toString());
			}
			buffer.append("------------------------------\n");
		}
		return buffer.toString();
	}
}
