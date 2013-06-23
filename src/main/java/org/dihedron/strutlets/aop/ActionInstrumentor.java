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

package org.dihedron.strutlets.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ActionInstrumentor {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionInstrumentor.class);
	
	/**
	 * Makes up and returns the name of the proxy class that will stub the action's 
	 * methods through its static methods.
	 * 
	 * @param action
	 *   the action whose proxy's name is to be retrieved.
	 * @return
	 *   the name of the proxy class.
	 */
	public static String makeProxyClassName(Class<? extends Action> action) {
		return action.getName() + "$Proxy";
	}
	
	/**
	 * Makes up and returns the name of the static method that will proxy the 
	 * given action method.
	 * 
	 * @param method
	 *   the method whose proxy's name is being retrieved.
	 * @return
	 *   the name of the static proxy method.
	 */
	public static String makeProxyMethodName(Method method) {
		return "_" + method.getName() + "$Stub";
	}

	/**
	 * The Javassist class pool used to create and stored synthetic classes.
	 */
	private ClassPool classpool;
		
	/**
	 * Default constructor, initialises the internal Javassist class pool with
	 * the default instance.
	 */
	public ActionInstrumentor() {
		this(new ClassPool());
	}
	
	/**
	 * Constructor.
	 *
	 * @param classpool
	 *   the Javassist class pool to generate AOP classes. 
	 */
	public ActionInstrumentor(ClassPool classpool) {
		this.classpool = classpool;
	}
	
	public Class<?> instrument(Class<? extends Action> action) throws StrutletsException {
		return instrument(action, null);
	}
	
	public Class<?> instrument(Class<? extends Action> action, Map<Method, Method> methods) throws StrutletsException {
		try {
			CtClass generator = getClassGenerator(action);
			for(Method method : enumerateInvocableMethods(action)) {
				logger.trace("instrumenting method '{}'...", method.getName());
				instrumentMethod(generator, action, method);
			}
			logger.trace("sealing and loading the proxy class");
			Class<?> proxy = generator.toClass(action.getClassLoader(), null);
			
			// now fill the map with methods and their proxies 
			if(methods != null) {
				methods.clear();
				
				outerloop:
				for(Method actionMethod : enumerateInvocableMethods(action)) {
					String proxyMethodName = makeProxyMethodName(actionMethod);
					for(Method proxyMethod : proxy.getDeclaredMethods()) {
						if(proxyMethod.getName().equals(proxyMethodName)) {
							methods.put(actionMethod, proxyMethod);
							continue outerloop;
						}						
					}
				}
			}
			return proxy;
		} catch(CannotCompileException e) {
			logger.error("error sealing the proxy class for '{}'", action.getSimpleName());
			throw new StrutletsException("error sealing proxy class for action '" + action.getSimpleName() + "'", e);
		}
	}
	
	private CtClass getClassGenerator(Class<? extends Action> action) throws StrutletsException {
		CtClass generator = null;
		String proxyname = makeProxyClassName(action);
		try {
			logger.trace("trying to retrieve generator '{}' for class '{}' from class pool...", proxyname, action.getSimpleName());
			generator = classpool.get(proxyname);
			generator.defrost();
			logger.trace("... generator found!");
		} catch (NotFoundException e1) {
			logger.trace("... generator not found in class pool, adding...");
			classpool.insertClassPath(new ClassClassPath(action));
			generator = classpool.makeClass(proxyname);			
			try {
				CtField log = CtField.make("private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(" + proxyname + ".class);", generator);
				generator.addField(log);
				logger.trace("... generator added, with built-in SLF4J support");
			} catch (CannotCompileException e2) {
				logger.error("error compiling SLF4J logger expression", e2);
				throw new StrutletsException("error compiling AOP code in class creation", e2);
			}			
		}
		return generator;
	}
	
	private Collection<Method> enumerateInvocableMethods(Class<? extends Action> action) {
		Map<String, Method> methods = new HashMap<String, Method>();
		
		// walk up the class hierarchy and gather methods as we go
		Class<?> clazz = action;
    	while(clazz != null && clazz != Object.class) { 
    		Method[] declared = clazz.getDeclaredMethods();
    		for(Method method : declared) {
    			if(methods.containsKey(method.getName())) {
    				logger.trace("discarding duplicate method '{}' coming from class '{}'...", method.getName(), method.getDeclaringClass().getSimpleName());
    			} else if(Modifier.isStatic(method.getModifiers())) {
    				logger.trace("discarding static method '{}' coming from class '{}'...", method.getName(), method.getDeclaringClass().getSimpleName());
    			} else if(!method.isAnnotationPresent(Invocable.class)){
    				logger.trace("discarding non-invocable method '{}' coming from class '{}'...", method.getName(), method.getDeclaringClass().getSimpleName());
    			} else {
    				logger.trace("adding invocable method '{}' coming from class '{}'...", method.getName(), method.getDeclaringClass().getSimpleName());
    				methods.put(method.getName(),  method);
    			}
    		}
    		clazz = clazz.getSuperclass();
    	}	
    	return methods.values();
	}
	
		
	private CtMethod instrumentMethod(CtClass generator, Class<? extends Action> action, Method method) throws StrutletsException {
		
		String methodName = makeProxyMethodName(method);
		logger.trace("method '{}' will be proxied by '{}'", method.getName(), methodName);
		try {			
			StringBuilder code = new StringBuilder("public static final java.lang.String ")
				.append(methodName)
				.append("( org.dihedron.strutlets.actions.Action action ) {\n");
			StringBuilder traceFormat = new StringBuilder("\tlogger.trace(\"");
			StringBuilder traceArgs = new StringBuilder("");
					
			Annotation[][] annotations = method.getParameterAnnotations();
			Class<?>[] types = method.getParameterTypes();
					
			StringBuilder args = new StringBuilder();
			
			for(int i = 0; i < types.length; ++i) {
				In in = null;
				for(Annotation annotation : annotations[i]) {
					if(annotation instanceof In) {
						in = (In)annotation;
						break;
					}
				}
				if(in != null) {
					String parameter = in.value();
					logger.trace("{}-th parameter '{}' is annotated with @In", i, in.value());					
					code
						.append("\t")
						.append(types[i].getCanonicalName())
						.append(" arg")
						.append(i).append(" = (")
						.append(types[i].getCanonicalName()).append(") ")
						.append("org.dihedron.strutlets.ActionContext.findValueInScopes(\"")
						.append(parameter)
						.append("\", new org.dihedron.strutlets.annotations.Scope[] {");
					boolean first = true;
					for(Scope scope : in.scopes()) {
						code
							.append(first ? "" : ", ")
							.append("org.dihedron.strutlets.annotations.Scope.")
							.append(scope);
						first = false;
					}
					code.append(" });\n");
//					code.append("\tlogger.trace(\"arg").append(i).append(" is '{}'\", arg").append(i).append(");\n");
					traceFormat.append("arg").append(i).append(" => '{}', ");
					traceArgs.append(traceArgs.length() > 0 ? ", " : "").append("arg").append(i);
					
				} else {
					logger.warn("{}-th parameter has no @In annotation!", i);
					if(!types[i].isPrimitive()) {
						logger.trace("{}-th parameter will be passed in as a null object", i);
						code.append("\t").append(types[i].getCanonicalName()).append(" arg").append(i).append(" = null;\n");
						traceFormat.append("arg").append(i).append(" => null, ");
					} else {
						logger.trace("{}-th parameter is a primitive type", i);
						if(types[i] == Boolean.TYPE) {
							logger.trace("{}-th parameter will be passed in as a boolean 'false'", i);
							code.append("\tboolean arg").append(i).append(" = false;\n");
							traceFormat.append("arg").append(i).append(" => false, ");
						} else if(types[i] == Character.TYPE) {
							logger.trace("{}-th parameter will be passed in as a character ' '", i);
							code.append("\tchar arg").append(i).append(" = ' ';\n");
							traceFormat.append("arg").append(i).append(" => ' ', ");
						} else if(types[i] == Byte.TYPE) {
							logger.trace("{}-th parameter will be passed in as a byte '0'", i);
							code.append("\tbyte arg").append(i).append(" = 0;\n");
							traceFormat.append("arg").append(i).append(" => 0, ");																				
						} else if(types[i] == Short.TYPE) {
							logger.trace("{}-th parameter will be passed in as a short '0'", i);
							code.append("\tshort arg").append(i).append(" = 0;\n");
							traceFormat.append("arg").append(i).append(" => 0, ");							
						} else if(types[i] == Integer.TYPE) {
							logger.trace("{}-th parameter will be passed in as an integer '0'", i);
							code.append("\tint arg").append(i).append(" = 0;\n");
							traceFormat.append("arg").append(i).append(" => 0, ");
						} else if(types[i] == Long.TYPE) {
							logger.trace("{}-th parameter will be passed in as a long '0'", i);
							code.append("\tlong arg").append(i).append(" = 0;\n");
							traceFormat.append("arg").append(i).append(" => 0, ");							
						} else if(types[i] == Float.TYPE) {
							logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
							code.append("\tfloat arg").append(i).append(" = 0.0;\n");
							traceFormat.append("arg").append(i).append(" => 0.0, ");							
						} else if(types[i] == Double.TYPE) {
							logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
							code.append("\tdouble arg").append(i).append(" = 0.0;\n");
							traceFormat.append("arg").append(i).append(" => 0.0, ");
						}
					}
				}
				args.append(args.length() > 0 ? ", arg" : "arg").append(i);				
			}
			logger.trace("traceFormat: '{}'", traceFormat);
			logger.trace("traceArgs  : '{}'", traceArgs);
			if(traceArgs.length() > 0) {
				traceFormat.setLength(traceFormat.length() - 2);
				code.append(traceFormat).append("\", ").append(traceArgs).append(");\n");
			}
			code
				.append("\tjava.lang.String result = ((")
				.append(action.getCanonicalName())
				.append(")$1).")
				.append(method.getName())
				.append("(")
				.append(args)
				.append(");\n");
			code.append("\tlogger.trace(\"result is '{}'\", result);\n");
			code.append("\treturn result;\n").append("}");
		
			logger.trace("compiling code:\n{}'", code);
		
			CtMethod proxyMethod = CtNewMethod.make(code.toString(), generator);
			generator.addMethod(proxyMethod);
			return proxyMethod;
			
		} catch (CannotCompileException e) {
			logger.error("error compiling AOP code in method creation", e);
			throw new StrutletsException("error compiling AOP code in method creation", e);
		} catch (SecurityException e) {
			logger.error("security violation getting declared method '" + methodName + "'", e);
			throw new StrutletsException("security violation getting declared method '" + methodName + "'", e);
		}		
	}
}
