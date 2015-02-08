/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.targets;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.actions.PortletMode;
import org.dihedron.strutlets.actions.Result;
import org.dihedron.strutlets.actions.WindowState;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.renderers.impl.JspRenderer;
import org.dihedron.strutlets.targets.registry.TargetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Target {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Target.class);
		
	/**
	 * A reference to the unique identifier of the target whose additional data 
	 * are stored in this object. 
	 */
	private TargetId id;
	
	/**
	 * Whether the action implements idempotent (that is, reiterable and thus fit 
	 * to be used in a render URL) or non-idempotent business logic.
	 */
	private boolean idempotent = DEFAULT_IDEMPOTENT;
	
	/**
  	 * Whether the (non-idempotent) target result should be considered cacheable;
	 * by setting this flag to {@code true} you are telling the framework that it
	 * can cache the result and replay it when the same submit is repeated multiple
	 * times; this effectively bypasses the non-idempotent code execution and
	 * always presents the user with the same result. By default an action is 
	 * assumed to be non cacheable, and effectively is if it maanipulates the 
	 * output stream or is non-deterministic (e.g the result depends on the time
	 * at which it was executed).	
	 */
	private boolean cacheable = DEFAULT_CACHEABLE;
	
	/**
	 * The class object of the Action class containing the executable code (the
	 * method) implementing the target's business logic.
	 */
	private Class<?> action;
	
	/**
	 * The method thatj implements the target's business logic.
	 */
	private Method factory;

	/**
	 * The method that implements the target's business logic.
	 */
	private Method method;
	
	/**
	 * The static proxy method that collects parameters from the vaious scopes 
	 * before invoking the actional action's business method; this method's 
	 * implementation is provided as a stub by the framework, by inspecting the
	 * action at bootstrap time and generating bytecode dynamically.
	 */
	private Method proxy;
	
	/**
	 * The pattern used to create JSP URLs.
	 */
	private String jspUrlPattern = TargetRegistry.DEFAULT_HTML_PATH_PATTERN;
	
	/**
	 * The name of the interceptor stack to be used with this action.
	 */
	private String interceptors;
	
	/**
	 * The map of expected results.
	 */
	private Map<String, Result> results = Collections.synchronizedMap(new HashMap<String, Result>());
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   a reference to the unique identifier of the target whose data are held
	 *   by this instance.
	 */
	public Target(TargetId id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of the target.
	 * 
	 * @return
	 *   the id of the target.
	 */
	public TargetId getId() {
		return id;
	}
	
	/**
	 * Returns the class object containing the executable code of this target's 
	 * business logic.
	 * 
	 * @return 
	 *   the class object containing the executable code of this target's 
	 *   business logic.
	 */
	public Class<?> getActionClass() {
		return this.action;
	}
	
	/**
	 * Sets the class object containing the executable code of this target's 
	 * business logic.
	 * 
	 * @param action
	 *   the class object containing the executable code of this target's 
	 *   business logic.
	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setActionClass(Class<?> action) {
		this.action = action;
		return this;
	}
	
	/**
	 * Returns the reference to the factory method capable of allocating and
	 * instance of the AbstractAction class implementing this target.
	 * 
	 * @return 
	 *   the reference to the containing action's factory method.
	 */
	public Method getFactoryMethod() {
		return this.factory;
	}	
	
	/**
	 * Sets the reference to the factory method capable of allocating and
	 * instance of the AbstractAction class implementing this target.
	 * 
	 * @param method
	 *   the reference to the containing action's factory method.
	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setFactoryMethod(Method method) {
		this.factory = method;
		return this;
	}

	/**
	 * Returns the reference to method implementing this target's business logic.
	 * 
	 * @return 
	 *   the reference to the method implementing this target's business logic.
	 */
	public Method getActionMethod() {
		return this.method;
	}	
	
	/**
	 * Sets the reference to the method implementing this target's business logic.
	 * 
	 * @param method
	 *   the reference to the method implementing this target's business logic.
	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setActionMethod(Method method) {
		this.method = method;
		return this;
	}
	
	/**
	 * Returns the static, framework-generated proxy method for the action's
	 * business logic method.
	 * 
	 * @return
	 *   the static proxy method.
	 */
	public Method getProxyMethod() {
		return this.proxy;
	}
	
	/**
	 * Sets the reference to the static, framework-generated proxy method for 
	 * the action's business logic method.
	 * 
	 * @param proxy
	 *   the static proxy method.
	 * @return
	 *   the object itself, for metod chaining.
	 */
	public Target setProxyMethod(Method proxy) {
		this.proxy = proxy;
		return this;
	}
	
	/**
	 * Returns whether the methodName implements idempotent business logic, which 
	 * makes it fit to be the target of a render URL, or non-idempotent logic,
	 * which restraints its utility to action, event and resource phases only, 
	 * whose execution is strictly under the user's control..
	 * 
	 * @return
	 *   whether the method is idempotent.
	 */
	public boolean isIdempotent() {
		return idempotent;
	}
	
	/**
	 * Sets whether the methodName implements idempotent (repeatable) business logic
	 * or it is a one-shot-at-a-time action methodName.
	 * 
	 * @param idempotent
	 *   whether the methodName is idempotent.
	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setIdempotent(boolean idempotent) {
		this.idempotent = idempotent;
		logger.trace("target '{}' {} idempotent", id, idempotent ? "is" : "is not");
		return this;
	}
	
	/**
	 * Returns whether the (non-idempotent) target result should be considered 
	 * cacheable; if cacheable, the framework will store the result and replay it 
	 * when the same submit is repeated multiple times; this effectively bypasses 
	 * the non-idempotent code execution and always presents the user with the 
	 * same result. By default an action is assumed to be non cacheable, and 
	 * effectively is if it happens to manipulates the output stream or is 
	 * non-deterministic (e.g the result depends on the time at which it was 
	 * executed).
	 * 
	 * @return
	 *   whether the result of the action is cacheable.
	 */
	public boolean isCacheable() {
		return !idempotent && cacheable;
	}
	
	/**
	 * Sets whether the (non-idempotent) target result should be considered 
	 * cacheable; if cacheable, the framework will store the result and replay it 
	 * when the same submit is repeated multiple times; this effectively bypasses 
	 * the non-idempotent code execution and always presents the user with the 
	 * same result. By default an action is assumed to be non cacheable, and 
	 * effectively is if it happens to manipulates the output stream or is 
	 * non-deterministic (e.g the result depends on the time at which it was 
	 * executed).
	 * 
	 * @param cacheable
	 *   whether the result of the action is cacheable.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public Target setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
		logger.trace("target '{}' {} cacheable", id, cacheable ? "is" : "is not");
		if(idempotent && this.cacheable) {
			logger.warn("target {} is idempotent and also cacheable, but it should not be: check your @Invocable annotation", id);
		}
		return this;
	}
	
	/**
	 * Returns the pattern used to create JSP URLs for JSP-rendered results
	 * that have not been declared in the annotation.
	 * 
	 * @return
	 *   the URL pattern used for auto-configured JSP-rendered results.
	 */
	public String getJspUrlPattern() {
		return this.jspUrlPattern;
	}
	
	/**
	 * Sets the value of the HTML views path pattern for automagic actions.
	 * 
	 * @param pattern
	 *   the pattern to be used to JSP path reconstruction at runtime.
	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setJspUrlPattern(String pattern) {
		if(Strings.isValid(pattern)) {
			this.jspUrlPattern = Strings.trim(pattern);
		}
		logger.trace("target '{}' has URL pattern '{}'", id, this.jspUrlPattern);
		return this;
	}	
	
	/**
	 * Retrieves the id of the interceptors stack.
	 * 
	 * @return
	 *   the name of the interceptors stack.
	 */
	public String getInterceptorStackId() {
		return interceptors;
	}
	
	/**
	 * Sets the name of the interceptors stack.
	 * 
	 * @param interceptors
	 *   the name of the interceptors stack.
 	 * @return 
	 *   the object itself, for method chaining.
	 */
	public Target setInterceptorsStackId(String interceptors) {
		if(Strings.isValid(interceptors)) {
			this.interceptors = interceptors;
		}
		logger.trace("target '{}' has interceptors stack '{}'", id, this.interceptors);
		return this;
	}
		
	public void addDeclaredResults(Invocable invocable) {
		logger.trace("auto-configuring results of '{}'...", id);
		for(org.dihedron.strutlets.annotations.Result annotation : invocable.results()) {
			addDeclaredResult(annotation);
		}
		logger.trace("... done auto-configuring results of '{}'", id);
	}

	public void addDeclaredResult(org.dihedron.strutlets.annotations.Result annotation) {
		String id = annotation.value();
		String renderer = annotation.renderer();
		PortletMode mode = PortletMode.fromString(annotation.mode());
		WindowState state = WindowState.fromString(annotation.state());
		String data = annotation.data();
		if(!Strings.isValid(data) && renderer.equalsIgnoreCase(JspRenderer.ID)) {
			data = makeJspUrl(id, mode, state);
			logger.trace("adding result '{}' with mode '{}', state '{}' and (auto-configured) data '{}'", id, mode, state, data);								
		} else {
			logger.trace("adding result '{}' with mode '{}', state '{}' and data '{}'", id, mode, state, data);				
		}
		Result result = new Result(id, renderer, data, mode, state);
		this.results.put(id, result);
		
	}
	
	public void addUndeclaredResult(String value) {
		String id = value;
		String renderer = DEFAULT_RENDERER;
		PortletMode mode = DEFAULT_MODE;
		WindowState state = DEFAULT_STATE;
		String data = makeJspUrl(id, mode, state);
		logger.trace("adding (auto-configured) result '{}' with mode '{}', state '{}' and data '{}'", id, mode, state, data);								
		Result result = new Result(id, renderer, data, mode, state);
		this.results.put(id, result);		
	}
	
	/**
	 * Returns the map of result identifiers and <code>Result</code> objects.
	 * 
	 * @return
	 *   the map of result identifiers and <code>Result</code> objects.
	 */
	public Map<String, Result> getResults() {
		return results;
	}
	
	/**
	 * Returns the <code>Result</code> object corresponding to the given
	 * result string, or null if none found. If the target belongs to an auto-
	 * configured action and no result could be found, the methodName attempts to
	 * reconstruct the information and returns it, after having added it to the
	 * set of valid results. 
	 * 
	 * @param rid
	 *   a result string (e.g. "success", "error").
	 * @return
	 *   the <code>Result</code> object corresponding to the given result string.
	 */
	public Result getResult(String rid) {
		assert(Strings.isValid(rid));
		Result result = results.get(rid);
		if(result == null) {
			logger.trace("result '{}' is not present yet, auto-configuring...", rid);
			addUndeclaredResult(rid);
		}
		return results.get(rid);
	}
	
	/**
	 * Returns a pretty printed, complex representation of the object as a string.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("target('").append(id.toString()).append("') {\n");
		buffer.append("  action      ('").append(id.getActionName()).append("')\n");
		buffer.append("  method      ('").append(id.getMethodName()).append("')\n");
		buffer.append("  proxy       ('").append(proxy.getName()).append("')\n");
		buffer.append("  idempotent  ('").append(this.isIdempotent()).append("')\n");
		buffer.append("  cacheable   ('").append(this.isCacheable()).append("')\n");
		buffer.append("  url pattern ('").append(this.getJspUrlPattern()).append("')\n");
		buffer.append("  stack       ('").append(interceptors).append("')\n");
		buffer.append("  javaclass   ('").append(action.getCanonicalName()).append("')\n");
		if(!results.isEmpty()) {
			buffer.append("  results {\n");
			for(Entry<String, Result> result : results.entrySet()) {
				buffer.append("    result  ('").append(result.getKey()).append("') { \n");
				buffer.append("      renderer ('").append(result.getValue().getRenderer()).append("')\n");
				buffer.append("      data     ('").append(result.getValue().getData()).append("')\n");
				buffer.append("      mode     ('").append(result.getValue().getPortletMode()).append("')\n");
				buffer.append("      state    ('").append(result.getValue().getWindowState()).append("')\n");
				buffer.append("    }\n");
			}
			buffer.append("  }\n");
		}
		buffer.append("}\n");
		return buffer.toString();
	}	
	
	/**
	 * Creates the path to a JSP for a given target and result combination, according 
	 * to the input pattern and starting from the given root HTML files directory.
	 * Both parameter can be overridden via the initialisation configuration parameters.
	 * 
	 * @param result
	 *   the target's result.
	 * @param mode
	 *   the new portlet mode.
	 * @param state
	 *   the new window state.
	 * @return
	 *   the URL of the JSP-renderered page for the given result.
	 */
	private String makeJspUrl(String result, PortletMode mode, WindowState state) {
		String path = this.jspUrlPattern
						.replaceAll("\\$\\{action\\}", id.getActionName())
						.replaceAll("\\$\\{method\\}", id.getMethodName())
						.replaceAll("\\$\\{mode\\}", mode.toString())
						.replaceAll("\\$\\{state\\}", state.toString())
						.replaceAll("\\$\\{result\\}", result);
		logger.debug("path for target: '{}', mode: '{}', state: '{}', result: '{}' is '{}'", id, mode, state, result, path);
		return path;
	}	
	
	/**
	 * By default a methodName is assumed to be non-idempotent, that is it cannot be
	 * invoked multiple times with the same parameteres yelding the same result.
	 * This makes it (by default) unfit to be used in a render phase, because
	 * render URLs can be invoked as many times as the container sees fit, by the
	 * book.
	 */
	private static final boolean DEFAULT_IDEMPOTENT = false;
	
	/**
	 * Whether the (non-idempotent) target result should be considered cacheable;
	 * by setting this flag to {@code true} you are telling the framework that it
	 * can cache the result and replay it when the same submit is repeated multiple
	 * times; this effectively bypasses the non-idempotent code execution and
	 * always presents the user with the same result. By default an action is 
	 * assumed to be non cacheable, and effectively is if it maanipulates the 
	 * output stream or is non-deterministic (e.g the result depends on the time
	 * at which it was executed). 
	 */
	private static final boolean DEFAULT_CACHEABLE = false;
	
	/**
	 * The default renderer, to be used when no renderer is specified.
	 */
	private static final String DEFAULT_RENDERER = JspRenderer.ID;
	
	/**
	 * The default portlet mode, to used when no portlet mode is specified.
	 */
	private static final PortletMode DEFAULT_MODE = PortletMode.SAME;
	
	/**
	 * The default window state, to be used when no window state is specified.
	 */
	private static final WindowState DEFAULT_STATE = WindowState.SAME;
	
}
