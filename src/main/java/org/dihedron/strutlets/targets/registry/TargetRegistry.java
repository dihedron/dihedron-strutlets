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

package org.dihedron.strutlets.targets.registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.Event;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.dihedron.strutlets.targets.TargetData;
import org.dihedron.strutlets.targets.TargetId;
import org.dihedron.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class containing the set of information available for the pool of supported 
 * actions; the information is stored in here by loading it from the configuration
 * file or by detecting an action's properties at runtime. This information can 
 * be used to instantiate new <code>Action</code>s and to get the output renderers 
 * for their results. 
 * 
 * @author Andrea Funto'
 */
public class TargetRegistry {
	
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
	private Map<TargetId, TargetData> store = new HashMap<TargetId, TargetData>(); 
	
	/**
	 * The set of event-to-target mappings.
	 */
	private Map<String, TargetId> events = new HashMap<String, TargetId>();
			
	/**
	 * The root directory to be used for <code>Action</code>s auto-configured 
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
	 * for self-configuring <code>Action</code>s lacking result URLs,</li>
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
	 * Registers a new target (as a {@code TargetId}, {@code TargetData} pair)
	 * into the registry.
	 * 
	 * @param action
	 *   the class of the action implementing the target's business logic.
	 * @param method
	 *   the method implementing the target's business logic.
	 * @param invocable
	 *   the method annotation, from which some information might be extracted.
	 * @param interceptors
	 *   the name of the interceptor stack to be used for the given action.
	 */
	public void addTarget(Class<? extends Action> action, Method method, Invocable invocable, String interceptors) {
		logger.info("adding target '{}!{}'", action.getSimpleName(), method.getName());
		TargetId id = new TargetId(action, method);
		
		// instantiate the information object
		TargetData data = new TargetData(id);
		data.setAction(action);
		data.setMethod(method);
		data.setIdempotent(invocable.idempotent());
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
	 * Retrieves the {@code TargetData} object corresponding to the given target
	 * identifier.
	 * 
	 * @param id
	 *   the target identifier.
	 * @return
	 *   the {@code TargetData} object; if none found in the registry, an
	 *   exception is thrown.
	 * @throws StrutletsException
	 *   if no @{code TargetData} object could be found for the given id.
	 */
	public TargetData getTarget(TargetId id) throws StrutletsException {
		if(!store.containsKey(id)) {
			logger.debug("repository does not contain info for target '{}'", id);
			throw new StrutletsException("Invalid target : '" + id.toString() + "'");
		}
		return store.get(id);		
	}
	
	/**
	 * Returns the @{code TargetData} corresponding to the action and method,
	 * as expressed in the given target string.
	 *  
	 * @param target
	 *   a string representing the action, with or without the method being invoked
	 *   on it; thus it can be the action name (in which case the default method 
	 *   "execute" is assumed) or the full target ("MyAction!myMethod").
	 * @return
	 *   the @{code TargetData} object corresponding to the given combination 
	 *   of action name and method name, if found. If no @{code TargetData} can 
	 *   be found an exception is thrown.
	 * @throws StrutletsException 
	 *   if the string is not a valid target or no target can be found corresponding 
	 *   to it.
	 */
	public TargetData getTarget(String target) throws StrutletsException {		 
		return getTarget(new TargetId(target));
	}

	/**
	 * Returns the @{code TargetData} corresponding to the action and method,
	 * as expressed in the given target string.
	 *  
	 * @param action
	 *   a string representing the action (e.g. "MyAction").
	 * @param method
	 *   a string representing the method (e.g. "myMethod").
	 * @return
	 *   the @{code TargetData} object corresponding to the given combination 
	 *   of action name and method name, if found. If no @{code TargetData} can 
	 *   be found an exception is thrown.
	 * @throws StrutletsException 
	 *   if the string is not a valid target or no target can be found corresponding 
	 *   to it.
	 */
	public TargetData getTarget(String action, String method) throws StrutletsException {			
		return getTarget(new TargetId(action, method));
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
	 * <li>lookup the <code>TargetData</code> for the given tagert name.</li>
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
	 * <li>lookup the <code>TargetData</code> for the given tagert name.</li>
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
	 * <li>lookup the <code>TargetData</code> for the given tagert name.</li>
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

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("");
		
		if(!store.isEmpty()) {
			for(Entry<TargetId, TargetData> entry : store.entrySet()) {
				buffer.append("----------- ACTION -----------\n");
				buffer.append(entry.getValue().toString());
			}
			buffer.append("------------------------------\n");
		}
		return buffer.toString();
	}	
	

	
}
