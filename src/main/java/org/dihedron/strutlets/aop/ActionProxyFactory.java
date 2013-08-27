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
import org.dihedron.strutlets.exceptions.DeploymentException;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that creates the proxy for a given action. The proxy creation must 
 * be performed all at once because Javassist applies several mechanisms such as
 * freezing and classloading that actually consolidate a class internal status 
 * and load its bytecode into the classloader, eventually making it unmodifieable.
 * By inspecting and creating proxy methods in one shot, this class performs all
 * operations on the proxy class in one single shot, then converts the synthetic
 * class into bytecode when no further modifications (such as method additions) 
 * can be expected.
 *    
 * @author Andrea Funto'
 */
public class ActionProxyFactory {
	
	/**
	 * The name of the factory method on the stub class.
	 */
	private static final String FACTORY_METHOD_NAME = "_makeAction";
	
	private static final String PROXY_CLASS_NAME_PREFIX = "";
	private static final String PROXY_CLASS_NAME_SUFFIX = "$Proxy";

	private static final String PROXY_METHOD_NAME_PREFIX = "_";
	private static final String PROXY_METHOD_NAME_SUFFIX = "";
	
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionProxyFactory.class);
	
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
		return PROXY_CLASS_NAME_PREFIX + action.getName() + PROXY_CLASS_NAME_SUFFIX;
	}
	
	/**
	 * Makes up and returns the name of the static factory method that each proxy
	 * class will implement in order to enable instantiation of new classes without
	 * having to invoke Class.forName("").newInstance().
	 *  
	 * @param action
	 *   the action to create a factory method for.
	 * @return
	 *   the name of the factory method.
	 */
	public static String makeFactoryMethodName(Class<? extends Action> action) {
		return FACTORY_METHOD_NAME;
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
		return PROXY_METHOD_NAME_PREFIX + method.getName() + PROXY_METHOD_NAME_SUFFIX;
	}

	/**
	 * The Javassist class pool used to create and stored synthetic classes.
	 */
	private ClassPool classpool;
		
	/**
	 * Default constructor, initialises the internal Javassist class pool with
	 * the default instance.
	 */
	public ActionProxyFactory() {
		this(new ClassPool());
	}
	
	/**
	 * Constructor.
	 *
	 * @param classpool
	 *   the Javassist class pool to generate AOP classes. 
	 */
	public ActionProxyFactory(ClassPool classpool) {
		this.classpool = classpool;
	}
	
	/**
	 * Instruments an action, returning the proxy class containing one static method 
	 * for each <code>@Invocable</code> method in the original class or in any
	 * of its super-classes (provided they are not shadowed through inheritance).
	 * 
	 * @param action
	 *   the action class to be instrumented. 
	 * @return
	 *   the proxy <code>Class</code>.
	 * @throws StrutletsException
	 *
	public Class<?> instrument(Class<? extends Action> action) throws StrutletsException {
		return instrument(action, null);
	}
	*/
	
	/**
	 * Instruments an action, returning the proxy class containing one static method 
	 * for each <code>@Invocable</code> method in the original class or in any
	 * of its super-classes (provided they are not shadowed through inheritance).
	 * 
	 * @param action
	 *   the action class to be instrumented.
	 * @return
	 *   the proxy object containing information about the <code>Action</code>
	 *   factory method, its proxy class and the static methods proxying each of 
	 *   the original <code>Action</code>'s invocable methods.
	 * @throws StrutletsException
	 */
	public ActionProxy makeActionProxy(Class<? extends Action> action/*, Map<Method, Method> methods*/) throws DeploymentException {		
		try {
			ActionProxy proxy = new ActionProxy();
			Map<Method, Method> methods = new HashMap<Method, Method>();
			
			CtClass generator = getClassGenerator(action);
			createFactoryMethod(generator, action);
			for(Method method : enumerateInvocableMethods(action)) {
				logger.trace("instrumenting method '{}'...", method.getName());
				instrumentMethod(generator, action, method);
			}
			
			
			// fill the proxy class 
			logger.trace("sealing and loading the proxy class");			
			Class<?> proxyClass = generator.toClass(action.getClassLoader(), null);			
			proxy.setProxyClass(proxyClass);
			
			// fill the map with methods and their proxies 
			outerloop:
			for(Method actionMethod : enumerateInvocableMethods(action)) {
				String proxyMethodName = makeProxyMethodName(actionMethod);
				for(Method proxyMethod : proxyClass.getDeclaredMethods()) {
					if(proxyMethod.getName().equals(proxyMethodName)) {
						methods.put(actionMethod, proxyMethod);
						continue outerloop;
					}						
				}
			}
			proxy.setMethods(methods);
			
			// now add the factory (constructor) method
			Method factory = proxyClass.getDeclaredMethod(makeFactoryMethodName(action));
			proxy.setFactoryMethod(factory);
			
			return proxy;
		} catch(CannotCompileException e) {
			logger.error("error sealing the proxy class for '{}'", action.getSimpleName());
			throw new DeploymentException("error sealing proxy class for action '" + action.getSimpleName() + "'", e);
		} catch (SecurityException e) {
			logger.error("error accessing the factory method for class '{}'", action.getSimpleName());
			throw new DeploymentException("error accessing the factory method for class '" + action.getSimpleName() + "'", e);
		} catch (NoSuchMethodException e) {
			logger.error("factory method for class '{}' not found", action.getSimpleName());
			throw new DeploymentException("factory method for class '" + action.getSimpleName() + "' not found", e);
		}
	}
	
	/**
	 * Generates a <code>CtClass</code> in the Javassist <code>ClassPool</code>
	 * to represent the new proxy.
	 * 
	 * @param action
	 *   the action for which a proxy must be created.
	 * @return
	 *   the <code>CtClass</code> object.
	 * @throws StrutletsException
	 */
	private CtClass getClassGenerator(Class<? extends Action> action) throws DeploymentException {
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
				throw new DeploymentException("error compiling AOP code in class creation", e2);
			}			
		}
		return generator;
	}
	
	/**
	 * Walks the class hierarchy up to <code>Object</code>, discarding non
	 * <code>@Invocable</code> methods, static methods and all methods from
	 * super-classes that have been overridden in extending classes.
	 * 
	 * @param action
	 *   the action whose methods are to be enumerated.
	 * @return
	 *   a collection of <code>@Invocable</code>, non-static and non-overridden
	 *   methods.
	 */
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
	
	private CtMethod createFactoryMethod(CtClass generator, Class<? extends Action> action) throws DeploymentException {
		String factoryName = makeFactoryMethodName(action);
		logger.trace("action '{}' will be created via '{}'", action.getSimpleName(), factoryName);
		
		if(Modifier.isAbstract(action.getModifiers())) {
			logger.error("cannot instantiate abstract class '{}'", action.getCanonicalName());
			throw new DeploymentException("cannot instantiate abstract class '" + action.getCanonicalName() + "'");
		}
		
		try {						
			StringBuilder code = new StringBuilder("public static final ").append(action.getCanonicalName()).append(" ").append(factoryName).append("() {\n");
			code.append("\tlogger.trace(\"entering factory method...\");\n");			
			code.append("\t").append(action.getCanonicalName()).append(" action = new ").append(action.getCanonicalName()).append("();\n");
			code.append("\tlogger.trace(\"... leaving factory method\");\n");
			code.append("\treturn action;\n").append("}");		
			logger.trace("compiling code:\n{}'", code);
		
			CtMethod factoryMethod = CtNewMethod.make(code.toString(), generator);
			generator.addMethod(factoryMethod);
			return factoryMethod;			
		} catch (CannotCompileException e) {
			logger.error("error compiling AOP code in factory method creation", e);
			throw new DeploymentException("error compiling AOP code in factory method creation", e);
		}				
	}
	
	/**
	 * Creates the Java code to proxy an action method. The code will also provide
	 * parameter injection (for <code>@In</code> annotated parameters) and basic
	 * profiling to measure how long it takes for the business method to execute.
	 * Each proxy method is actually static, so there is no need to have an 
	 * instance of the proxy class to invoke it and there's no possibility that
	 * any state is kept between invocations.
	 *  	
	 * @param generator
	 *   the Javassist <code>CtClass</code> used to generate newstatic methods.
	 * @param action
	 *   the action class to instrument.
	 * @param method
	 *   the specific action methpd to instrument.
	 * @return
	 *   an instance of <code>CtMethod</code>, repsenting a static proxy method.
	 * @throws DeploymentException
	 */
	private CtMethod instrumentMethod(CtClass generator, Class<? extends Action> action, Method method) throws DeploymentException {
		
		String methodName = makeProxyMethodName(method);
		logger.trace("method '{}' will be proxied by '{}'", method.getName(), methodName);
		try {						
			StringBuilder code = new StringBuilder("public static final java.lang.String ")
				.append(methodName)
				.append("( org.dihedron.strutlets.actions.Action action ) {\n");
			code.append("\tlogger.trace(\"entering stub method...\");\n");			
			code.append("\tjava.lang.StringBuilder trace = new java.lang.StringBuilder();\n");
			code.append("\tjava.lang.Object value = null;\n");
					
			Annotation[][] annotations = method.getParameterAnnotations();
			Class<?>[] types = method.getParameterTypes();
					
			StringBuilder args = new StringBuilder();
			
			for(int i = 0; i < types.length; ++i) {
				code.append("\t//\n\t// retrieving argument no. ").append(i).append(" (").append(types[i].getCanonicalName()).append(")\n\t//\n");
				In in = null;
				for(Annotation annotation : annotations[i]) {
					if(annotation instanceof In) {
						in = (In)annotation;
						break;
					}
				}
				if(in != null) {
					if(types[i].isPrimitive()) {
						logger.error("primitive types are not supported on annotated parameters (check parameter {}: type is '{}')", i, types[i].getCanonicalName());
						throw new DeploymentException("Primitive types are not supported as @In parameters: check parameter no. " + i + " (type is '" + types[i].getCanonicalName() + "')");
					}
					String parameter = in.value();
					logger.trace("{}-th parameter '{}' is annotated with @In", i, in.value());
					code.append("\tvalue = org.dihedron.strutlets.ActionContext.findValueInScopes(\"").append(parameter).append("\", new org.dihedron.strutlets.annotations.Scope[] {");
					boolean first = true;
					for(Scope scope : in.scopes()) {
						code
							.append(first ? "" : ", ")
							.append("org.dihedron.strutlets.annotations.Scope.")
							.append(scope);
						first = false;
					}
					code.append(" });\n");
					
					if(!types[i].isArray()) {
						// if parameter is not an array, pick the first element))
						code.append("\tif(value != null && value.getClass().isArray()) {\n\t\tvalue = ((Object[])value)[0];\n\t}\n");
					}					
					code.append("\t").append(types[i].getCanonicalName()).append(" arg").append(i).append(" = (").append(types[i].getCanonicalName()).append(") value;\n");
					code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => '\").append(arg").append(i).append(").append(\"', \");\n");
					
				} else {
					logger.warn("{}-th parameter has no @In annotation!", i);
					if(!types[i].isPrimitive()) {
						logger.trace("{}-th parameter will be passed in as a null object", i);						
						code.append("\t").append(types[i].getCanonicalName()).append(" arg").append(i).append(" = null;\n");
						code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => null, \");\n");
					} else {
						logger.trace("{}-th parameter is a primitive type", i);
						if(types[i] == Boolean.TYPE) {
							logger.trace("{}-th parameter will be passed in as a boolean 'false'", i);
							code.append("\tboolean arg").append(i).append(" = false;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => false, \");\n");
						} else if(types[i] == Character.TYPE) {
							logger.trace("{}-th parameter will be passed in as a character ' '", i);
							code.append("\tchar arg").append(i).append(" = ' ';\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => ' ', \");\n");
						} else if(types[i] == Byte.TYPE) {
							logger.trace("{}-th parameter will be passed in as a byte '0'", i);
							code.append("\tbyte arg").append(i).append(" = 0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
						} else if(types[i] == Short.TYPE) {
							logger.trace("{}-th parameter will be passed in as a short '0'", i);
							code.append("\tshort arg").append(i).append(" = 0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
						} else if(types[i] == Integer.TYPE) {
							logger.trace("{}-th parameter will be passed in as an integer '0'", i);
							code.append("\tint arg").append(i).append(" = 0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
						} else if(types[i] == Long.TYPE) {
							logger.trace("{}-th parameter will be passed in as a long '0'", i);
							code.append("\tlong arg").append(i).append(" = 0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
						} else if(types[i] == Float.TYPE) {
							logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
							code.append("\tfloat arg").append(i).append(" = 0.0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0.0, \");\n");
						} else if(types[i] == Double.TYPE) {
							logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
							code.append("\tdouble arg").append(i).append(" = 0.0;\n");
							code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0.0, \");\n");
						}
					}
				}
				args.append(args.length() > 0 ? ", arg" : "arg").append(i);	
				code.append("\n");
			}
			code.append("\tif(trace.length() > 0) {\n\t\ttrace.setLength(trace.length() - 2);\n\t\tlogger.debug(trace.toString());\n\t}\n\n");
			
			code.append("\t//\n\t// invoking proxied method\n\t//\n");
			code.append("\tlong millis = java.lang.System.currentTimeMillis();\n");
			code
				.append("\tjava.lang.String result = ((")
				.append(action.getCanonicalName())
				.append(")$1).")
				.append(method.getName())
				.append("(")
				.append(args)
				.append(");\n");
			code.append("\tlogger.debug(\"result is '{}' (execution took {} ms)\", result, new java.lang.Long((java.lang.System.currentTimeMillis() - millis)).toString());\n");
			code.append("\tlogger.trace(\"... leaving stub method\");\n");
			code.append("\treturn result;\n").append("}");
		
			logger.trace("compiling code:\n{}'", code);
		
			CtMethod proxyMethod = CtNewMethod.make(code.toString(), generator);
			generator.addMethod(proxyMethod);
			return proxyMethod;
			
		} catch (CannotCompileException e) {
			logger.error("error compiling AOP code in method creation", e);
			throw new DeploymentException("error compiling AOP code in method creation", e);
		} catch (SecurityException e) {
			logger.error("security violation getting declared method '" + methodName + "'", e);
			throw new DeploymentException("security violation getting declared method '" + methodName + "'", e);
		}		
	}
}
