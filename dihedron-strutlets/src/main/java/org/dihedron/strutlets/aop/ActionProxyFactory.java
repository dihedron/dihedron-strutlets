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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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

import org.dihedron.commons.utils.Types;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.InOut;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
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
	public static String makeProxyClassName(Class<?> action) {
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
	public static String makeFactoryMethodName(Class<?> action) {
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
	 *   the proxy object containing information about the <code>AbstractAction</code>
	 *   factory method, its proxy class and the static methods proxying each of 
	 *   the original <code>AbstractAction</code>'s invocable methods.
	 * @throws StrutletsException
	 */
	public ActionProxy makeActionProxy(Class<?> action) throws DeploymentException {		
		try {
			ActionProxy proxy = new ActionProxy();
			Map<Method, Method> methods = new HashMap<Method, Method>();
			
			CtClass generator = getClassGenerator(action);
			
			// adds the static method that creates or retrieves the 
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
	 * The proxy class will have a static factory method, used to retrieve the 
	 * actual inner action intance used at runtime without resorting to reflection.
	 * Depeding on the structure of the <code>Action</code> object it will or
	 * won't be cached: as a matter of fact, it will be scanned for the presence 
	 * of non-static firlds, and if found the action will be non-cacheable (since 
	 * there will be fields on which there would be concurrent access if the same 
	 * action were used to service multiple requests at once). Thus, when creating 
	 * the code for the factory method, some reflection is employed to check if 
	 * there are no instance fields and only in that case the <code>singleton</code>
	 * field will be pre-initialised with a reference to a singleton instance of
	 * the action. If any non-static field is found, then each invocation will
	 * cause a new action instance to be created.
	 * 
	 * @param action
	 *   the action for which a proxy must be created.
	 * @return
	 *   the <code>CtClass</code> object.
	 * @throws StrutletsException
	 */
	private CtClass getClassGenerator(Class<?> action) throws DeploymentException {
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
				// add the SLF4J logger
				CtField log = CtField.make("private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(" + proxyname + ".class);", generator);
				generator.addField(log);
				
				if(hasInstanceFields(action)) {
					logger.trace("factory method will renew action instances at each invocation");
				} else {
					logger.trace("factory method will reuse a single, cached action instance");
					// add the singleton instance; it will be used to store the single instance for actions that					
					// can be cached (see method comments to see when an action can be cached and reused)
					CtField singleton = CtField.make("private final static " + action.getCanonicalName() + " singleton = new " + action.getCanonicalName() + "();", generator);
					generator.addField(singleton);					
				}
				logger.trace("... generator added");
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
	private static Collection<Method> enumerateInvocableMethods(Class<?> action) {
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
	
	/**
	 * Checks if a class (and its hierarchy) has any non-static fields.
	 * 
	 * @param action
	 *   the class to be scanned for the presence of instance fields.
	 * @return
	 *   whether the class and its super-classes declare any non-static field.
	 */
	private static boolean hasInstanceFields(Class<?> action) {
		boolean found = false;
		// walk up the class hierarchy and gather instance fields as we og
		Class<?> clazz = action;
    	while(clazz != null && clazz != Object.class) { 
    		Field[] declared = clazz.getDeclaredFields();
    		for(Field field : declared) {
    			if(!Modifier.isStatic(field.getModifiers())) {
    				found = true;
    				break;
    			}
    		}
    		clazz = clazz.getSuperclass();
    	}	
		return found;
	}
	
	/**
	 * Creates the static factory method that retrieves the instance of action to
	 * be used in the actual invocation. The way of retrieving the action instance
	 * varies depending on whether the action class has, or has not, instance 
	 * fields: in the former case the action cannot be recycled or used concurrently 
	 * since this would probably result in data corruption, in the latter case a 
	 * single instance of the action can be reused across multiple requests, even
	 * concurrently, thus resulting in reduced memory usage, heap fragmentation
	 * and object instantiation overhead at runtime. 
	 * 
	 * @param generator
	 *   the Javassist class generator.
	 * @param action
	 *   the class of the action object.
	 * @return
	 *   the Javassist object representing the factory method.
	 * @throws DeploymentException
	 */
	private CtMethod createFactoryMethod(CtClass generator, Class<?> action) throws DeploymentException {
		String factoryName = makeFactoryMethodName(action);
		logger.trace("action '{}' will be created via '{}'", action.getSimpleName(), factoryName);
		
		// check if there is a no-args contructor
		try {
			action.getConstructor();
		} catch (SecurityException e) {
			logger.error("error trying to access constructor for class '" + action.getSimpleName() + "'", e);
			throw new DeploymentException("Error trying to access constructor for class '" + action.getSimpleName() + "'", e);
		} catch (NoSuchMethodException e) {
			logger.error("class '" + action.getSimpleName() + "' does not have a no-args constructor, please ensure it has one or it cannot be deployed", e);
			throw new DeploymentException("Class '" + action.getSimpleName() + "' does not have a no-args constructor, please ensure it has one or it cannot be deployed", e);
		}
		
		try {						
			StringBuilder code = new StringBuilder("public static final ").append(action.getCanonicalName()).append(" ").append(factoryName).append("() {\n");
			code.append("\tlogger.trace(\"entering factory method...\");\n");
			// now analyse the action class and all its parent classes, checking 
			// if it has any non-static field, and then decide whether we can reuse
			// the single cached instance or we need to create a brand new instance 
			// at each invocation
			if(hasInstanceFields(action)) {
				code.append("\tlogger.trace(\"instantiating brand new non-cacheable object\");\n");
				code.append("\t").append(action.getCanonicalName()).append(" action = new ").append(action.getCanonicalName()).append("();\n");
			} else {
				code.append("\tlogger.trace(\"reusing single, cached instance\");\n");
				code.append("\t").append(action.getCanonicalName()).append(" action = singleton;\n");
			}
			code.append("\tlogger.trace(\"... leaving factory method\");\n");
			code.append("\treturn action;\n").append("}");		
			logger.trace("compiling code:\n\n{}\n", code);
		
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
	private CtMethod instrumentMethod(CtClass generator, Class<?> action, Method method) throws DeploymentException {
		
		String methodName = makeProxyMethodName(method);
		logger.trace("method '{}' will be proxied by '{}'", method.getName(), methodName);
		try {
			StringBuilder postCode = new StringBuilder("\t//\n\t// post action execution: store @Out parameters into scopes\n\t//\n\n");
			
			StringBuilder code = new StringBuilder("public static final java.lang.String ")
				.append(methodName)
				.append("( java.lang.Object action ) {\n\n");
			code.append("\tlogger.trace(\"entering stub method...\");\n");			
			code.append("\tjava.lang.StringBuilder trace = new java.lang.StringBuilder();\n");
			code.append("\tjava.lang.Object value = null;\n");
			code.append("\n");		
			Annotation[][] annotations = method.getParameterAnnotations();
			
			Type[] types = method.getGenericParameterTypes();
					
			StringBuilder args = new StringBuilder();
			
			for(int i = 0; i < types.length; ++i) {
				String arg = prepareArgument(i, types[i], annotations[i], code, postCode);
				args.append(args.length() > 0 ? ", " : "").append(arg);
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
			
			// code executed after the action has been fired, e.g. storing [in]out parameters into scopes
			code.append("\n");
			code.append(postCode);
			code.append("\n");
			code.append("\tlogger.debug(\"result is '{}' (execution took {} ms)\", result, new java.lang.Long((java.lang.System.currentTimeMillis() - millis)).toString());\n");
			code.append("\tlogger.trace(\"... leaving stub method\");\n");
			code.append("\treturn result;\n").append("}");
		
			logger.trace("compiling code:\n\n{}\n", code);
		
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
	
	private String prepareArgument(int i, Type type, Annotation[] annotations, StringBuilder preCode, StringBuilder postCode) throws DeploymentException {
				
		In in = null;
		Out out = null;
		InOut inout = null;
		for(Annotation annotation : annotations) {
			if(annotation instanceof In) {
				in = (In)annotation;
			} else if (annotation instanceof Out) {
				out = (Out)annotation;
			} else if(annotation instanceof InOut) {
				inout = (InOut)annotation;
			}
		}
		if(inout != null) {
			logger.trace("preparing input argument...");
			// safety check: verify that no @In or @Out parameters are specified
			if(in != null) {
				logger.warn("attention! parameter {} is annotated with incompatible annotations @InOut an @In: @In will be ignored", i);
			}
			if(out != null) {
				logger.warn("attention! parameter {} is annotated with incompatible annotations @InOut an @Out: @Out will be ignored", i);
			}			
			return prepareInOutArgument(i, type, inout, preCode, postCode); 			
		} else if(in != null && out == null) {
			logger.trace("preparing input argument...");
			return prepareInputArgument(i, type, in, preCode); 
		} else if(in == null && out != null) {
			logger.trace("preparing output argument...");
			return prepareOutputArgument(i, type, out, preCode, postCode);
		} else if(in != null && out != null) {
			logger.trace("preparing input/output argument...");
			return prepareInputOutputArgument(i, type, in, out, preCode, postCode);
		} else {
			logger.trace("preparing non-annotated argument...");
			return prepareNonAnnotatedArgument(i, (Class<?>)type, preCode);
		}
	}
	
	
	private String prepareInputArgument(int i, Type type, In in, StringBuilder preCode) throws DeploymentException {
		
		preCode.append("\t//\n\t// preparing input argument no. ").append(i).append(" (").append(Types.getAsString(type)).append(")\n\t//\n");

		if(Types.isSimple(type) && ((Class<?>)type).isPrimitive()) {
			logger.error("primitive types are not supported on annotated parameters (check parameter {}: type is '{}')", i, Types.getAsString(type));
			throw new DeploymentException("Primitive types are not supported as @In parameters: check parameter no. " + i + " (type is '" + Types.getAsString(type) + "')");
		}
		
		if(in.value().trim().length() == 0) {
			logger.error("input parameters' storage name must be explicitly specified through the @In annotation's value (check parameter {}: @In's value is '{}')", i, in.value());
			throw new DeploymentException("Input parameters's storage name must be explicitly specified through the @In annotation's value: check parameter no. " + i + " (@In's value is '" + in.value() + "')");									
		}
		
		String parameter = in.value();
		logger.trace("{}-th parameter is annotated with @In('{}')", i, in.value());
		preCode.append("\tvalue = org.dihedron.strutlets.ActionContext.findValueInScopes(\"").append(parameter).append("\", new org.dihedron.strutlets.annotations.Scope[] {");
		boolean first = true;
		for(Scope scope : in.scopes()) {
			preCode.append(first ? "" : ", ").append("org.dihedron.strutlets.annotations.Scope.").append(scope);
			first = false;
		}
		preCode.append(" });\n");
		
		if(Types.isSimple(type) && !((Class<?>)type).isArray()) {
			// if parameter is not an array, pick the first element
			preCode.append("\tif(value != null && value.getClass().isArray()) {\n\t\tvalue = ((Object[])value)[0];\n\t}\n");
		}					
		preCode.append("\t").append(Types.getAsRawType(type)).append(" in").append(i).append(" = (").append(Types.getAsRawType(type)).append(") value;\n");
		preCode.append("\ttrace.append(\"in").append(i).append("\").append(\" => '\").append(in").append(i).append(").append(\"', \");\n");
		preCode.append("\n");
		return "in" + i;
	}
	
	private String prepareOutputArgument(int i, Type type, Out out, StringBuilder preCode, StringBuilder postCode) throws DeploymentException {
		
		if(!Types.isGeneric(type)) {
			logger.error("output parameters must be generic, and of reference type $<?> (check parameter no. {}: type is '{}'", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must generic, and of reference type $<?> (check parameter no. " + i + ": type is '" + ((Class<?>)type).getCanonicalName() + " '");
		}
		
		if(!Types.isOfClass(type, $.class))	{		
			logger.error("output parameters must be wrapped in typed reference holders ($) (check parameter {}: type is '{}')", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must be wrapped in typed reference holders ($): check parameter no. " + i + " (type is '" + ((Class<?>)type).getCanonicalName() + "')");									
		}
		
		if(out.value().trim().length() == 0) {
			logger.error("output parameters' storage name must be explicitly specified through the @Out annotation's value (check parameter {}: @Out's value is '{}')", i, out.value());
			throw new DeploymentException("Output parameters's name must be explicitly specified through the @Out annotation's value: check parameter no. " + i + " (@Out value is '" + out.value() + "')");									
		}		
		
		Type wrapped = Types.getParameterTypes(type)[0]; 
		logger.trace("output parameter no. {} is of type $<{}>", i, Types.getAsString(wrapped));
		
		//
		// code executed BEFORE the action fires, to prepare input parameters
		//
		preCode.append("\t//\n\t// preparing output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(")\n\t//\n");
		
		logger.trace("{}-th parameter is annotated with @Out('{}')", i, out.value());
		// NOTE: no support for generics in Javassist: drop types (which would be dropped by type erasure anyway...)
		// code.append("\torg.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append("> out").append(i).append(" = new org.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append(">();\n");
		preCode.append("\torg.dihedron.strutlets.aop.$ out").append(i).append(" = new org.dihedron.strutlets.aop.$();\n");
		preCode.append("\n");
		
		//
		// code executed AFTER the action has returned, to store values into scopes
		//
		postCode.append("\t//\n\t// storing input/output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(") into scope ").append(out.scope()).append("\n\t//\n");
		postCode.append("\tvalue = out").append(i).append(".get();\n");
		postCode.append("\tif(value != null) {\n");
		postCode.append("\t\torg.dihedron.strutlets.ActionContext.storeValueIntoScope( \"").append(out.value()).append("\", ").append("org.dihedron.strutlets.annotations.Scope.").append(out.scope()).append(", value );\n");
		postCode.append("\t}\n");
		postCode.append("\n");
		
		return "out" + i;
	}
	
	private String prepareInputOutputArgument(int i, Type type, In in, Out out, StringBuilder preCode, StringBuilder postCode) throws DeploymentException {
		
		if(!Types.isGeneric(type)) {
			logger.error("output parameters must be generic, and of reference type $<?> (check parameter no. {}: type is '{}'", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must generic, and of reference type $<?> (check parameter no. " + i + ": type is '" + ((Class<?>)type).getCanonicalName() + " '");
		}
		
		if(!Types.isOfClass(type, $.class))	{		
			logger.error("output parameters must be wrapped in typed reference holders ($) (check parameter {}: type is '{}')", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must be wrapped in typed reference holders ($): check parameter no. " + i + " (type is '" + ((Class<?>)type).getCanonicalName() + "')");									
		}

		if(in.value().trim().length() == 0) {
			logger.error("input parameters' storage name must be explicitly specified through the @In annotation's value (check parameter {}: @In's value is '{}')", i, in.value());
			throw new DeploymentException("Input parameters's storage name must be explicitly specified through the @In annotation's value: check parameter no. " + i + " (@In's value is '" + in.value() + "')");									
		}
		
		if(out.value().trim().length() == 0) {
			logger.error("output parameters' storage name must be explicitly specified through the @Out annotation's value (check parameter {}: @Out's value is '{}')", i, out.value());
			throw new DeploymentException("Output parameters's name must be explicitly specified through the @Out annotation's value: check parameter no. " + i + " (@Out value is '" + out.value() + "')");									
		}		
		
		
		Type wrapped = Types.getParameterTypes(type)[0]; 
		logger.trace("input/output parameter no. {} is of type $<{}>", i, Types.getAsString(wrapped));
		
		//
		// code executed BEFORE the action fires, to prepare input parameters
		//		
		preCode.append("\t//\n\t// preparing input/output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(")\n\t//\n");
				
		String parameter = in.value();
		logger.trace("{}-th parameter is annotated with @In('{}') and @Out('{}')", i, in.value(), out.value());
		preCode.append("\tvalue = org.dihedron.strutlets.ActionContext.findValueInScopes(\"").append(parameter).append("\", new org.dihedron.strutlets.annotations.Scope[] {");
		boolean first = true;
		for(Scope scope : in.scopes()) {
			preCode.append(first ? "" : ", ").append("org.dihedron.strutlets.annotations.Scope.").append(scope);
			first = false;
		}
		preCode.append(" });\n");
		
		if(Types.isSimple(wrapped) && !((Class<?>)wrapped).isArray()) {
			// if parameter is not an array, pick the first element
			preCode.append("\tif(value != null && value.getClass().isArray()) {\n\t\tvalue = ((Object[])value)[0];\n\t}\n");
		}					

		// NOTE: no support for generics in Javassist: drop types (which would be dropped by type erasure anyway...)
		// code.append("\torg.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append("> inout").append(i).append(" = new org.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append(">();\n");
		preCode.append("\torg.dihedron.strutlets.aop.$ inout").append(i).append(" = new org.dihedron.strutlets.aop.$();\n");
		preCode.append("\tinout").append(i).append(".set(value);\n");
		preCode.append("\ttrace.append(\"inout").append(i).append("\").append(\" => '\").append(inout").append(i).append(".get()).append(\"', \");\n");
		preCode.append("\n");
		
		//
		// code executed AFTER the action has returned, to store values into scopes
		//
		postCode.append("\t//\n\t// storing input/output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(") into scope ").append(out.scope()).append("\n\t//\n");
		postCode.append("\tvalue = inout").append(i).append(".get();\n");
		postCode.append("\tif(value != null) {\n");
		postCode.append("\t\torg.dihedron.strutlets.ActionContext.storeValueIntoScope( \"").append(out.value()).append("\", ").append("org.dihedron.strutlets.annotations.Scope.").append(out.scope()).append(", value );\n");
		postCode.append("\t}\n");
		postCode.append("\n");
		return "inout" + i;
	}

	private String prepareInOutArgument(int i, Type type, InOut inout, StringBuilder preCode, StringBuilder postCode) throws DeploymentException {
		
		if(!Types.isGeneric(type)) {
			logger.error("output parameters must be generic, and of reference type $<?> (check parameter no. {}: type is '{}'", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must generic, and of reference type $<?> (check parameter no. " + i + ": type is '" + ((Class<?>)type).getCanonicalName() + " '");
		}
		
		if(!Types.isOfClass(type, $.class))	{		
			logger.error("output parameters must be wrapped in typed reference holders ($) (check parameter {}: type is '{}')", i, ((Class<?>)type).getCanonicalName());
			throw new DeploymentException("Output parameters must be wrapped in typed reference holders ($): check parameter no. " + i + " (type is '" + ((Class<?>)type).getCanonicalName() + "')");									
		}

		if(inout.value().trim().length() == 0) {
			logger.error("input/output parameters' storage name must be explicitly specified through the @InOut annotation's value (check parameter {}: @InOut's value is '{}')", i, inout.value());
			throw new DeploymentException("Input parameters's storage name must be explicitly specified through the @InOut annotation's value: check parameter no. " + i + " (@InOut's value is '" + inout.value() + "')");									
		}
				
		Type wrapped = Types.getParameterTypes(type)[0]; 
		logger.trace("input/output parameter no. {} is of type $<{}>", i, Types.getAsString(wrapped));
		
		//
		// code executed BEFORE the action fires, to prepare input parameters
		//		
		preCode.append("\t//\n\t// preparing input/output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(")\n\t//\n");
				
		String parameter = inout.value();
		logger.trace("{}-th parameter is annotated with @InOut('{}') and @Out('{}')", i, inout.value());
		preCode.append("\tvalue = org.dihedron.strutlets.ActionContext.findValueInScopes(\"").append(parameter).append("\", new org.dihedron.strutlets.annotations.Scope[] {");
		boolean first = true;
		for(Scope scope : inout.from()) {
			preCode.append(first ? "" : ", ").append("org.dihedron.strutlets.annotations.Scope.").append(scope);
			first = false;
		}
		preCode.append(" });\n");
		
		if(Types.isSimple(wrapped) && !((Class<?>)wrapped).isArray()) {
			// if parameter is not an array, pick the first element
			preCode.append("\tif(value != null && value.getClass().isArray()) {\n\t\tvalue = ((Object[])value)[0];\n\t}\n");
		}					

		// NOTE: no support for generics in Javassist: drop types (which would be dropped by type erasure anyway...)
		// code.append("\torg.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append("> inout").append(i).append(" = new org.dihedron.strutlets.aop.$<").append(gt.getCanonicalName()).append(">();\n");
		preCode.append("\torg.dihedron.strutlets.aop.$ inout").append(i).append(" = new org.dihedron.strutlets.aop.$();\n");
		preCode.append("\tinout").append(i).append(".set(value);\n");
		preCode.append("\ttrace.append(\"inout").append(i).append("\").append(\" => '\").append(inout").append(i).append(".get()).append(\"', \");\n");
		preCode.append("\n");
		
		//
		// code executed AFTER the action has returned, to store values into scopes
		//
		postCode.append("\t//\n\t// storing input/output argument no. ").append(i).append(" (").append(Types.getAsString(wrapped)).append(") into scope ").append(inout.to()).append("\n\t//\n");
		postCode.append("\tvalue = inout").append(i).append(".get();\n");
		postCode.append("\tif(value != null) {\n");
		postCode.append("\t\torg.dihedron.strutlets.ActionContext.storeValueIntoScope( \"").append(inout.value()).append("\", ").append("org.dihedron.strutlets.annotations.Scope.").append(inout.to()).append(", value );\n");
		postCode.append("\t}\n");
		postCode.append("\n");
		return "inout" + i;
	}
	
	
	private String prepareNonAnnotatedArgument(int i, Class<?> type, StringBuilder code) throws DeploymentException {
		
		code.append("\t//\n\t// preparing non-annotated argument no. ").append(i).append(" (").append(Types.getAsString(type)).append(")\n\t//\n");
		
		logger.warn("{}-th parameter has no @In or @Out annotation!", i);
		if(!type.isPrimitive()) {
			logger.trace("{}-th parameter will be passed in as a null object", i);						
			code.append("\t").append(Types.getAsString(type)).append(" arg").append(i).append(" = null;\n");
			code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => null, \");\n");
		} else {
			logger.trace("{}-th parameter is a primitive type", i);
			if(type == Boolean.TYPE) {
				logger.trace("{}-th parameter will be passed in as a boolean 'false'", i);
				code.append("\tboolean arg").append(i).append(" = false;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => false, \");\n");
			} else if(type == Character.TYPE) {
				logger.trace("{}-th parameter will be passed in as a character ' '", i);
				code.append("\tchar arg").append(i).append(" = ' ';\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => ' ', \");\n");
			} else if(type == Byte.TYPE) {
				logger.trace("{}-th parameter will be passed in as a byte '0'", i);
				code.append("\tbyte arg").append(i).append(" = 0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
			} else if(type == Short.TYPE) {
				logger.trace("{}-th parameter will be passed in as a short '0'", i);
				code.append("\tshort arg").append(i).append(" = 0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
			} else if(type == Integer.TYPE) {
				logger.trace("{}-th parameter will be passed in as an integer '0'", i);
				code.append("\tint arg").append(i).append(" = 0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
			} else if(type == Long.TYPE) {
				logger.trace("{}-th parameter will be passed in as a long '0'", i);
				code.append("\tlong arg").append(i).append(" = 0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0, \");\n");
			} else if(type == Float.TYPE) {
				logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
				code.append("\tfloat arg").append(i).append(" = 0.0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0.0, \");\n");
			} else if(type == Double.TYPE) {
				logger.trace("{}-th parameter will be passed in as a float '0.0'", i);
				code.append("\tdouble arg").append(i).append(" = 0.0;\n");
				code.append("\ttrace.append(\"arg").append(i).append("\").append(\" => 0.0, \");\n");
			}
		}
		code.append("\n");
		return "arg" + i;
	}
}