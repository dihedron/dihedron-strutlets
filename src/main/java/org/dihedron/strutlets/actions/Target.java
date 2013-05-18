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

package org.dihedron.strutlets.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.regex.Regex;
import org.dihedron.utils.Strings;
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
	 * The name of the default <code>ActionInfo</code> method.
	 */
	public static final String DEFAULT_METHOD_NAME = "execute";
	
	/**
	 * By default a method is assumed to be non-idempotent, that is it cannot be
	 * invoked multiple times with the same parameteres yelding the same result.
	 * This makes it (by default) unfit to be used in a render phase, because
	 * render URLs can be invoked as many times as the container sees fit, by the
	 * book.
	 */
	public static final boolean DEFAULT_METHOD_IDEMPOTENT = false;
	
	/**
	 * The default pattern used to make up JSP URLs for automagic Actions.
	 */
	public static final String DEFAULT_PATH_PATTERN = "${rootdir}/${action}/${method}/${result}.jsp";
	
	/**
	 * The character separating action and method names in the target id.
	 */
	public static final String METHOD_SEPARATOR = "!";
	
	/**
	 * A Java regular expression matching a combination of action name and method 
	 * name; the original expression is 
	 * <code>^\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\s*!\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\s*$</code>
	 * and matches names of the form <code>MyAction!myMethod</code>, where
	 * the action identifier complies with the rules for Java class names and
	 * the method identifier complies with the best practices for Java methods
	 * (starting with a lowercase alphabetic character, followed by any alphanumeric
	 * character. 
	 */
	public static final String TARGET_REGEXP = "^\\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\\s*!\\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\\s*$";
	
	/**
	 * The regular expression used to extract action and method name from targets
	 * and to check if a give string represents a target.
	 */
	private static final Regex REGEX = new Regex(TARGET_REGEXP);	
	
	/**
	 * Checks whether the given string represents a valid target specification.
	 * 
	 * @param string
	 *   a string to be checked for compliance with target specifications; valid 
	 *   targets specifications are in the form "MyAction!myMethod".
	 * @return
	 *   whether the given string complies with a target specification. 
	 */
	public static final boolean isValidActionTarget(String string) {
		return Strings.isValid(string) && REGEX.matches(string);
	}
	
	/**
	 * Given the target specification in the &lt;action&gt;!&lt;method&gt;
	 * form (e.g. "MyAction!myMethod", where the method part is optional), returns 
	 * the name of the action ("MyAction" in the above example).
	 * 
	 * @param target
	 *   the target specification, including the method or not.
	 * @return
	 *   the action name.
	 */
	public static final String getActionName(String target) {	
		String action = null;
		if(Strings.isValid(target)) {
			if(target.contains(METHOD_SEPARATOR)) {
				action = target.substring(0, target.indexOf(METHOD_SEPARATOR)).trim();
			} else {
				action = target.trim();
			}
		}
		logger.trace("action: '{}'", action);
		return action;
	}
	
	/**
	 * Given the target specification in the &lt;action&gt;!&lt;method&gt;
	 * form (e.g. "MyAction!myMethod"), returns the name of the method ("myMethod" 
	 * in the above example); if the name of the method is empty, it returns the
	 * default method ("execute").
	 * 
	 * @param target
	 *   the action target specification, including the method.
	 * @return
	 *   the method name, or null if the target is incomplete.
	 */
	public static final String getMethodName(String target) {
		String method = null;
		if(Strings.isValid(target) && target.contains(METHOD_SEPARATOR)) {
			method = target.substring(target.indexOf(METHOD_SEPARATOR) + 1).trim();
		}
		if(!Strings.isValid(method)) {
			method = DEFAULT_METHOD_NAME;
		}
		logger.trace("method: '{}'", method);
		return method;
	}
	
	/**
	 * Makes up the name of the target from the name of the action and that of 
	 * the method.
	 * 
	 * @param action
	 *   the name of the action.
	 * @param method
	 *   the (optional) name of the method; if null or empty, the default name
	 *   ("execute") is used instead.
	 * @return
	 *   the name of the target, in the form "MyAction!myMethod".
	 */
	public static final String makeTargetName(String action, String method) {
		String meth = method;
		if(!Strings.isValid(action)) {
			logger.error("invalid action name");
			return null;
		}
		if(!Strings.isValid(meth)) {
			logger.trace("using default name for method");
			meth = DEFAULT_METHOD_NAME;
		}
		String result = action.trim() + METHOD_SEPARATOR + meth;
		logger.trace("target name is '{}'", result);
		return result;
	}
		
	/**
	 * The name (identifier) of the action.
	 */
	private String action;
	
	/**
	 * The name (identifier) of the method.
	 */
	private String method;
	
	/**
	 * Whether the action implements idempotent (that is, reiterable and thus fit 
	 * to be used in a render URL) or non-idempotent business logic.
	 */
	private boolean idempotent = DEFAULT_METHOD_IDEMPOTENT;
	
	/**
	 * Whether the target is automatically configured.
	 */
	private boolean automagic = false;
	
	/**
	 * The name of the Java class implementing the <code>Action</code>.
	 */
	private String classname;
	
	/**
	 * The Java package where the actions' factory will try to locate the Java
	 * object implementing the action if <code>classname</code> is not specified. 
	 */
	private String packagename;
	
	/**
	 * The directory where JSP pages for auto-configured actions are to be located.
	 */
	private String rootHtmlDirectory = "/";
	
	/**
	 * The pattern used to create JSP URLs.
	 */
	private String htmlPathPattern = DEFAULT_PATH_PATTERN;
	
	/**
	 * The name of the interceptor stack to be used with this action.
	 */
	private String interceptors;
	
	/**
	 * A map of initialisation parameters.
	 */
	private Map<String, String> parameters = new HashMap<String, String>();	
	
	/**
	 * The map of expected results.
	 */
	private Map<String, Result> results = Collections.synchronizedMap(new HashMap<String, Result>());
	
	/**
	 * Constructor.
	 * 
	 * @param target
	 *   the name of the action and method combination.
	 * @param automagic
	 *   whether the target is auto-configured.
	 */
	public Target(String target, boolean automagic) {
		assert(target != null);
		this.action = Target.getActionName(target);
		this.method = Target.getMethodName(target);
		this.automagic = automagic;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param action
	 *   the name of the action.
	 * @param method
	 *  the name of the method.
	 * @param automagic
	 *   whether the target is auto-configured.
	 */
	public Target(String action, String method, boolean automagic) {
		assert(action != null);
		this.action = action.trim();
		if(Strings.isValid(method)) {
			this.method = method.trim();
		} else {
			this.method = DEFAULT_METHOD_NAME;
		}
		this.automagic = automagic;
	}	
	
	/**
	 * Returns the target id, as a combination of action and method.
	 * 
	 * @return
	 *   the target id (e.g. "MyAction!myMethod").
	 */
	public String getId() {
		return makeTargetName(action, method);
	}
	
	/**
	 * Returns the target's action.
	 * 
	 * @return
	 *   the target's action.
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Returns the target's method.
	 * 
	 * @return
	 *   the target's method.
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * Returns whether the target is automatically configured.
	 * 
	 * @return
	 *   whether the target is automatically configured.
	 */
	public boolean isAutomagic() {
		return automagic;
	}

	/**
	 * Returns whether the method implements idempotent business logic, which 
	 * makes it fir to be the target odf a render URL, or non-idempotent logic,
	 * which restraints its utility to action and event phases only, whose 
	 * execution is strictly under the user's control..
	 * 
	 * @return
	 *   whether the method is idempotent.
	 */
	public boolean isIdempotent() {
		return idempotent;
	}
	
	/**
	 * Sets whether the method implements idempotent (repeatable) business logic
	 * or it is a one-shot-at-a-time action method.
	 * 
	 * @param idempotent
	 *   whether the method is idempotent.
	 */
	public void setIdempotent(boolean idempotent) {
		this.idempotent = idempotent;
	}
	
	/**
	 * Sets the fully qualified name of the Java class implementing the action.
	 *  
	 * @param classname
	 *   the fully qualified name of the Java class implementing the action.
	 */
	public void setClassName(String classname) {
		if(Strings.isValid(classname)) {
			this.classname = Strings.trim(classname);
		}
	}	
	
	/**
	 * Sets the name of the Java package (not including the trailing dot) 
	 * containing the Java class implementing the action.
	 * 
	 * @param packagename
	 *   the name of the Java package containing the action's class.
	 */
	public void setPackageName(String packagename) {
		if(Strings.isValid(packagename)) {
			this.packagename = Strings.trim(packagename);
		}
	}
	
	/**
	 * Sets the value of the HTML package (directory) to be used as the base directory
	 * for self-configuring <code>Action</code>s lacking result URLs. Fabricated
	 * URLs will conform to the following pattern: 
	 * &lt;root directory&gt;/&lt;action name&gt;/&lt;method name&gt;_&lt;result&gt;.jsp
	 * 
	 * @param rootHtmlDirectory
	 *   the root directory to be used as the starting point for for fabricated URLs.
	 */
	public void setRootHtmlDirectory(String rootHtmlDirectory) {
		if(Strings.isValid(rootHtmlDirectory)) {
			this.rootHtmlDirectory = Strings.trim(rootHtmlDirectory);
		}
		if(!this.rootHtmlDirectory.endsWith("/")) {
			this.rootHtmlDirectory = Strings.concatenate(this.rootHtmlDirectory, "/");
		}
	}
	
	/**
	 * Sets the value of the HTML views path pattern for automagic actions.
	 * 
	 * @param htmlPathPattern
	 *   the pattern to be used to JSP path reconstruction at runtime.
	 */
	public void setHtmlPathPattern(String htmlPathPattern) {
		if(Strings.isValid(htmlPathPattern)) {
			this.htmlPathPattern = Strings.trim(htmlPathPattern);
		}
	}	
	
	
	/**
	 * Retrieves the name of the class to be instantiated, either by returning 
	 * what has been explicitly configured via the <code>classname</code> XML
	 * tag, or by juxtaposing the <code>package</code> and the id of the action.
	 * 
	 * @return
	 *   the name of the class implementing the action.
	 */
	public String getClassName() {
		if(Strings.isValid(classname)) {
			return classname;
		}
		return packagename + "." + action;
	}
	
	/**
	 * Sets the name of the interceptors stack.
	 * 
	 * @param interceptors
	 *   the name of the interceptors stack.
	 */
	public void setInterceptorsStackId(String interceptors) {
		if(Strings.isValid(interceptors)) {
			this.interceptors = interceptors;
		}
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
	 * Adds a parameter to the set of initialisation parameters for the action.
	 * 
	 * @param key
	 *   the parameter key.
	 * @param value
	 *   the parameter value.
	 */
	void addParameter(String key, String value) {
		if(key != null) {
			parameters.put(key, value);
		}
	}

	/**
	 * Adds a parameter to the set of initialisation parameters for the action.
	 * 
	 * @param key
	 *   the parameter key.
	 * @param value
	 *   the parameter value.
	 */
	public void addParameters(Map<String, String> parameters) {
		if(parameters != null) {
			this.parameters.putAll(parameters);
		}
	}
	
	/**
	 * Returns the set of initialisation parameters for the action.
	 * 
	 * @return
	 *   the set of initialisation parameters for the action.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}	
	
	/**
	 * Adds a result to the set of expected results for this action method.
	 * 
	 * @param result
	 *   the expected result.
	 */
	public void addResult(Result result) {
		if(result != null) {
			results.put(result.getId(), result);
		}
	}
	
	/**
	 * Adds a result to the set of expected results for this action method.
	 * 
	 * @param id
	 *   the result id ("e.g. "success" or "error".
	 * @param type
	 *   the type of renderer to be used to render the result.
	 * @param mode
	 *   the mode in which the portlet should be put. 
	 * @param state
	 *   the windows state in which this JSP should be shown.
	 * @param url
	 *   the URL of the JSP page.
	 */
	@Deprecated
	public void addResult(String id, String type, String mode, String state, String url) {
		Result result = new Result(id, type, mode, state, url);
		results.put(id, result);
	}
	
	/**
	 * Adds a result to the set of expected results for this action method.
	 * 
	 * @param id
	 *   the result id ("e.g. "success" or "error".
	 * @param type
	 *   the type of renderer to be used to render the result.
	 * @param mode
	 *   the mode in which the portlet should be put. 
	 * @param state
	 *   the windows state in which this JSP should be shown.
	 * @param url
	 *   the URL of the JSP page.
	 */
	public void addResult(String id, ResultType type, PortletMode mode, WindowState state, String url) {
		Result result = new Result(id, type, mode, state, url);
		results.put(id, result);
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
	 * configured action and no result could be found, the method attempts to
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
		if(result == null && automagic) {			
			// ${rootdir}/${action}/${method}/${result}.jsp
			String url = htmlPathPattern
							.replaceAll("\\$\\{rootdir\\}", rootHtmlDirectory)
							.replaceAll("\\$\\{action\\}", action)
							.replaceAll("\\$\\{method\\}", method)
							.replaceAll("\\$\\{result\\}", rid);			
			
			Strings.concatenate(rootHtmlDirectory, action, "/", method, "_", rid, ".jsp");
			logger.trace("synthetic URL for result '{}' on action '{}', method '{}' is '{}'", rid, action, method, url);
			ResultType type = ResultType.JSP; 
			PortletMode mode = PortletMode.SAME;
			WindowState state = WindowState.SAME;
			result = new Result(rid, type, mode, state, url);
			results.put(rid, result);
		}
		return result;
	}
	
	/**
	 * Returns a pretty printed, complex representation of the object as a string.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("target('").append(action).append("!").append(method).append("') {\n");
		buffer.append("  action    ('").append(action).append("')\n");
		buffer.append("  method    ('").append(method).append("')\n");
		buffer.append("  automagic ('").append(automagic).append("')\n");
		buffer.append("  stack     ('").append(interceptors).append("')\n");
		buffer.append("  classname ('").append(classname).append("')\n");
		buffer.append("  package   ('").append(packagename).append("')\n");
		buffer.append("  javaclass ('").append(getClassName()).append("')\n");
		if(!parameters.isEmpty()) {
			buffer.append("  parameters {\n");
			for(Entry<String, String> parameter : parameters.entrySet()) {
				buffer.append("    parameter('").append(parameter.getKey()).append("') = '").append(parameter.getValue()).append("'\n");
			}
			buffer.append("  }\n");
		}
		if(!results.isEmpty()) {
			buffer.append("  results {\n");
			for(Entry<String, Result> result : results.entrySet()) {
				buffer.append("    result  ('").append(result.getKey()).append("') { \n");
				buffer.append("      mode  ('").append(result.getValue().getPortletMode()).append("')\n");
				buffer.append("      state ('").append(result.getValue().getWindowState()).append("')\n");
				buffer.append("      type  ('").append(result.getValue().getResultType()).append("')\n");
				buffer.append("      url   ('").append(result.getValue().getUrl()).append("')\n");
				buffer.append("    }\n");
			}
			buffer.append("  }\n");
		}
		buffer.append("}\n");
		
//		buffer.append("target\n");
//		buffer.append(String.format("+ action         : '%1$s'\n", action));
//		buffer.append(String.format("+ method         : '%1$s'\n", method));
//		buffer.append(String.format("+ automagic      : '%1$s'\n", automagic));
//		buffer.append(String.format("+ interceptors   : '%1$s'\n", interceptors));
//		buffer.append(String.format("+ class name     : '%1$s'\n", classname));
//		buffer.append(String.format("+ package        : '%1$s'\n", packagename));
//		buffer.append(String.format("+ java class     : '%1$s'\n", getClassName()));
//		if(!parameters.isEmpty()) {
//			buffer.append(" + parameters\n");
//			for(Entry<String, String> pentry : parameters.entrySet()) {
//				buffer.append("  + parameter\n");
//				buffer.append(String.format("   + key         : '%1$s'\n", pentry.getKey()));	
//				buffer.append(String.format("   + value       : '%1$s'\n", pentry.getValue()));
//			}
//		}
//		buffer.append("   + results\n");
//		for(Entry<String, Result> rentry : results.entrySet()) {
//			buffer.append("    + result\n");
//			buffer.append(String.format("     + id        : '%1$s'\n", rentry.getKey()));
//			buffer.append(String.format("     + mode      : '%1$s'\n", rentry.getValue().getPortletMode()));
//			buffer.append(String.format("     + state     : '%1$s'\n", rentry.getValue().getWindowState()));
//			buffer.append(String.format("     + url       : '%1$s'\n", rentry.getValue().getUrl()));
//		}
		return buffer.toString();
	}	
}
