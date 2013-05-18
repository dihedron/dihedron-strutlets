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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.ResultType;
import org.dihedron.strutlets.actions.Target;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.strutlets.annotations.Event;
import org.dihedron.strutlets.annotations.Interceptors;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Result;
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
public class ActionRegistry {
	
	/**
	 * The default pattern for JSP paths generation, for auto-configured targets.
	 */
	public static final String DEFAULT_HTML_PATH_PATTERN = "${rootdir}/${action}/${method}/${result}.jsp";
		
	/**
	 * Constructor.
	 */
	public ActionRegistry() {
		logger.info("creating action info repository");
	}
	
	/**
	 * Sets the value of the Java package to be used for self-configuring 
	 * <code>Action</code>s.
	 * 
	 * @param defaultActionPackage
	 *   the Java package to be used for self-configuring <code>Action</code>s.
	 */
	public void setDefaultActionPackage(String defaultActionPackage) {
		if(Strings.isValid(defaultActionPackage)) {
			this.defaultActionPackage = defaultActionPackage;
			logger.info("default java package for actions: '{}'", this.defaultActionPackage);
		}
	}
	
	/**
	 * Sets the value of the HTML package (directory) to be used as the base directory
	 * for self-configuring <code>Action</code>s lacking result URLs.
	 * 
	 * @param rootHtmlDirectory
	 *   the root directory to be used as the starting point for for fabricated URLs.
	 */
	public void setRootHtmlDirectory(String rootHtmlDirectory) {		
		if(Strings.isValid(rootHtmlDirectory)) {
			this.rootHtmlDirectory = rootHtmlDirectory;
			if(this.rootHtmlDirectory.endsWith("/")) {
				int index = this.rootHtmlDirectory.lastIndexOf('/');
				this.rootHtmlDirectory = this.rootHtmlDirectory.substring(0, index);
			}
		}
		logger.info("root directory for conjured up result URLs: '{}'", this.rootHtmlDirectory);
	}	
	
	/**
	 * Sets the value of the pattern according to which JSP pages will be located
	 * for auto-configured targets. If not specified, URLs will be conjured up 
	 * according to the the following pattern: 
	 * &lt;root directory&gt;/&lt;action name&gt;/&lt;method name&gt;/&lt;result&gt;.jsp
	 * 
	 * @param htmlPathPattern
	 *   the pattern to be used for locating auto-configured actions' JSPs.
	 */
	public void setHtmlPathPattern(String htmlPathPattern) {		
		if(Strings.isValid(htmlPathPattern)) {
			this.htmlPathPattern = htmlPathPattern;
		}
		logger.info("pattern for auto-configured targets' JSPs: '{}'", this.htmlPathPattern);
	}	
	
	/**
	 * Returns the <code>Target</code> corresponding to the action and method,
	 * as expressed in the given target.
	 *  
	 * @param target
	 *   a string representing the action, with or without the method being invoked
	 *   on it; thus it can be the action name (in which case the default method 
	 *   "execute" is assumed) or the full target ("MyAction!myMethod").
	 * @return
	 *   the <code>Target</code> object corresponding to the given combination 
	 *   of action name and method name, if found. If no <code>Target</code> can 
	 *   be found for the given key, an attempt is made to load the class and scan 
	 *   it for relevant information; the result of the scanning process is stored 
	 *   among the other cached informations before being returned, for future 
	 *   reference.
	 */
	public Target getTarget(String target) {
			
		String action = Target.getActionName(target);
		String method = Target.getMethodName(target);
		String targetName = Target.makeTargetName(action, method);
		if(!store.containsKey(targetName)) {
			logger.debug("repository does not contain info for action '{}', method '{}'", action, method);
			addTarget(action, method);
		}
		return store.get(targetName);
	}

	/**
	 * Returns the <code>Target</code> corresponding to the action as expressed
	 * in the given target. If the target string contains both action and method 
	 * name, the name of the action is extracted.
	 *  
	 * @param action
	 *   a string representing the action (e.g. "MyAction").
	 * @param method
	 *   a string representing the method (e.g. "myMethod").
	 * @return
	 *   the <code>ActionInfo</code> object corresponding to the given action name,
	 *   if found. If no <code>Target</code> can be found, an attempt is made
	 *   to load the class and scan it for relevant information; the result of the
	 *   scanning process is stored among the other cached informations before 
	 *   being returned, for future reference.
	 */
	public Target getTarget(String action, String method) {			
		return getTarget(Target.makeTargetName(action, method));
	}
		
	/**
	 * Stores information into the repository.
	 * 
	 * @param target
	 *   the label of the <code>Action</code> and method combination whose info 
	 *   is being stored.
	 * @param info
	 *   the action's target information (metadata).
	 */
	public void putTarget(String target, Target info) {
		store.put(target, info);
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
	public String getEventTarget(QName qname) {
		String key = qname.toString();
		logger.trace("looking up target information for '{}'", key);
		return events.get(key);
	}
	
	/**
	 * Stores the name of the target that will handle events of the given type.
	 * Each action method can declare its supported events, and if so its name
	 * is stored in a separate lookup map that helps identify the action that will 
	 * handle a given event when it arrives. The lookup sequence is:<ol>
	 * <li>lookup the name of the target for thegiven event <code>QName</code>;</li>
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
	 */
	public void putEventTarget(QName qname, String target) {
		if(Strings.isValid(target) && qname != null) {
			logger.trace("target '{}' will be invoked on events of type '{}'", target, qname.toString()); 
			events.put(qname.toString(),  target);
		}		
	}
	
	/**
	 * Auto-detects information about the given action/method from the class 
	 * annotations or by educated guesses on the name of result JSPs.
	 * 
	 * @param action
	 *   the name of the action.
	 * @param meth
	 *   the name of the method.
	 * @return
	 *   a <code>Target</code> object with information about the given action
	 *   and method.
	 */
	public void addTarget(String action, String meth) {
		
		Target info = null;
		String classname = null;
		
		classname = defaultActionPackage + "." + action;
		
		logger.trace("trying to auto-detect information from class '{}'", classname);
		
		try {		
			// try to load the class
			Class<?> clazz = Class.forName(classname);
			
			logger.trace("class '{}' loaded into virtual machine", classname);
		
			// check if the class is an Action, and if so proceed
			if(Action.class.isAssignableFrom(clazz)) {
				
				logger.trace("auto-configuring class '{}' as an Action", classname);								
								
				String stack = null;
				
				// try to get information about interceptors
				Interceptors interceptors = clazz.getAnnotation(Interceptors.class);
				if(interceptors != null) {
					stack = interceptors.value();
					logger.trace("interceptors: '{}'", stack);
				}
				
				// try to get information about methods
				Method method = clazz.getMethod(meth);
				if(method != null) {

					// instantiate the information object
					info = new Target(action, method.getName(), true);
					
					info.setPackageName(defaultActionPackage);
					info.setRootHtmlDirectory(rootHtmlDirectory);
					info.setHtmlPathPattern(htmlPathPattern);
					info.setClassName(classname);
					info.setInterceptorsStackId(stack);
					
					Invocable invocable = (Invocable)method.getAnnotation(Invocable.class);	
					if(invocable != null) {
						
						boolean idempotent = invocable.idempotent();
						logger.trace("target '{}!{}' {} idempotent", action, method.getName(), idempotent ? "is" : "is not");
						info.setIdempotent(idempotent);						
						
						logger.trace("auto-configuring events of '{}!{}'", action, method.getName());
						for(Event event : invocable.events()) {
							String name = event.value();
							String namespace = event.namespace();
							QName qname = new QName(namespace, name);
							events.put(qname.toString(), info.getId());
						}
						logger.trace("auto-configuring results of {}!{}", action, method.getName());
						for(Result result : invocable.results()) {
							String id = result.value();
							ResultType type = ResultType.JSP;
							PortletMode mode = PortletMode.fromString(result.mode());
							WindowState state = WindowState.fromString(result.state());
							String url = result.url();
							if(!Strings.isValid(url)) {
								url = makeJspPath(action, method.getName(), id, mode.toString(), state.toString());
								logger.trace(" > result '{}' with mode '{}', state '{}' and (auto-configured) url '{}'", 
										result.value(), result.mode(), result.state(), url);								
							} else {
								logger.trace(" > result '{}' with mode '{}', state '{}' and url '{}'", 
										result.value(), result.mode(), result.state(), url);								
							}
							info.addResult(id, type, mode, state, url);
						}
					} else {
						logger.trace("no annotations found for {}!{}", action, method.getName());
					}
				}
			} else {
				logger.trace("class '{}' is not an Action", classname);
			}
		} catch (ClassNotFoundException e) {
			logger.error("error loading class '" + classname + "'", e);
		} catch (SecurityException e) {
			logger.error("security exception error loading class '" + classname + "'", e);
		} catch (NoSuchMethodException e) {
			logger.error("method '" + meth + "' not found on class '" + classname + "'", e);
		}
		
		if(info != null) {
			if(!store.containsKey(info.getId())) {
				logger.debug("adding info about target '{}' to map", info.getId());
				store.put(info.getId(), info);
			} else {
				logger.debug("target '{}' is already configured", info.getId());
			}
		}
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder("");
		
		if(!store.isEmpty()) {
			for(Entry<String, Target> entry : store.entrySet()) {
				buffer.append("----------- ACTION -----------\n");
				buffer.append(entry.getValue().toString());
			}
			buffer.append("------------------------------\n");
		}
		return buffer.toString();
	}	
	
	/**
	 * Creates the path to a JSP for a given target and result combination, according 
	 * to the input pattern and starting from the given root HTML files directory.
	 * Both parameter can be overridden via the initialisation configuration parameters.
	 * 
	 * @param action
	 * @param method
	 * @param result
	 * @param mode
	 * @param state
	 * @return
	 */
	private String makeJspPath(String action, String method, String result, String mode, String state) {
		String path = this.htmlPathPattern
						.replaceAll("\\$\\{rootdir\\}", this.rootHtmlDirectory)
						.replaceAll("\\$\\{action\\}", action)
						.replaceAll("\\$\\{method\\}", method)
						.replaceAll("\\$\\{mode\\}", mode)
						.replaceAll("\\$\\{state\\}", state)
						.replaceAll("\\$\\{result\\}", result);
		logger.debug("path for action: '{}', method: '{}', mode: '{}', state: '{}', result: '{}' is '{}'", 
				action, method, mode, state, result, path);
		return path;
	}
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionRegistry.class);

	/**
	 * The actual set of actions' targets. 
	 */
	private Map<String, Target> store = Collections.synchronizedMap(new HashMap<String, Target>()); 
	
	/**
	 * The set of event-to-target mappings.
	 */
	private Map<String, String> events = Collections.synchronizedMap(new HashMap<String, String>());
			
	/**
	 * The Java package to be used for self-configuring <code>Action</code>s.
	 */
	private volatile String defaultActionPackage = "";	

	/**
	 * The root directory to be used for self-configuring <code>Action</code>s
	 * result URLs.
	 */
	private volatile String rootHtmlDirectory = "/";
	
	/**
	 * The pattern to be used for conjuring up JSP pages for auto-configured 
	 * targets. For a thorough discussion on format and accepted variables
	 * check out 
	 * {@link org.dihedron.strutlets.ActionController.InitParameter.RENDER_PATH_PATTERN InitParameter.RENDER_PATH_PATTERN}.
	 */	
	private volatile String htmlPathPattern = DEFAULT_HTML_PATH_PATTERN;
}
