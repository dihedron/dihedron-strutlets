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

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ActionProxyFactory {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionProxyFactory.class);

	/**
	 * The repository of generated classes.
	 */
	private ClassPool classpool;

	/**
	 * Constructor.
	 */
	public ActionProxyFactory() {
		classpool = new ClassPool();
	}
	
	/**
	 * Returns an instance of the AOP proxy for a given class, from the class pool 
	 * if already available, or by instantiating and loading a brand-new AOP proxy
	 * and then creating anew instance if not available yet.
	 * 
	 * @param action
	 *   the action for which a proxy must be retrieved.
	 * @return
	 *   an instance of the AOP proxy for the given class.
	 * @throws StrutletsException
	 */
	public Object getProxyFor(Class<? extends Action> action) throws StrutletsException {
		try {
			return getProxyClass(action).toClass().newInstance();
		} catch (InstantiationException e) {
			logger.error("error instantiating AOP proxy class", e);
			throw new StrutletsException("error instantiating AOP proxy class", e);
		} catch (IllegalAccessException e) {
			logger.error("illegal access creating AOP proxy class", e);
			throw new StrutletsException("illegal access creating AOP proxy class", e);
		} catch (CannotCompileException e) {
			logger.error("error compiling AOP code in class creation", e);
			throw new StrutletsException("error compiling AOP code in class creation", e);
		}		
	}
		
	/**
	 * Gets an instance of the generated AOP proxy.
	 * 
	 * @param action
	 *   the action proxied by the returned class. 
	 * @return
	 *   the proxy class for the given action class.
	 * @throws StrutletsException 
	 */
	private CtClass getProxyClass(Class<? extends Action> action) throws StrutletsException {
		CtClass proxy = null;
		String proxyname = makeProxyClassName(action);		
		try {
			logger.trace("trying to retrieve proxy '{}' from class pool...", proxyname);
			proxy = classpool.get(proxyname);
			proxy.defrost();
			logger.trace("... proxy found");
		} catch (NotFoundException e) {
			logger.info("... proxy not found in class pool, adding");
			classpool.insertClassPath(new ClassClassPath(action));
			String classname = makeProxyClassName(action);
			proxy = classpool.makeClass(classname);			
			try {
				CtField log = CtField.make("private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(" + classname + ".class);", proxy);
				proxy.addField(log);
				logger.info("... proxy added, with built-in SLF4J support");
			} catch (CannotCompileException e1) {
				logger.error("error compiling SLF4J logger expression", e);
				throw new StrutletsException("error compiling AOP code in class creation", e);
			}			
		}
		return proxy;
	}
	
	public void addProxyMethod(Class<? extends Action> action, Method method) throws StrutletsException {
		try {
			StringBuilder code = new StringBuilder("public java.lang.String ")
				.append(makeProxyMethodName(method))
				.append("( org.dihedron.strutlets.actions.Action action ) {\n");
					
			CtClass proxyClass = getProxyClass(action);
			Annotation[][] annotations = method.getParameterAnnotations();
			Class<?>[] types = method.getParameterTypes();
					
			StringBuilder args = new StringBuilder();
			for(int i = 0; i < types.length; ++i) {
				for(Annotation annotation : annotations[i]) {
					if(annotation instanceof In) {					
						In in = (In)annotation;
						String parameter = in.value();
	//					code
	//						.append("\t")
	//						.append(types[i].getCanonicalName())
	//						.append(" arg")
	//						.append(i).append(" = (")
	//						.append(types[i].getCanonicalName()).append(") ")
	//						.append("org.dihedron.strutlets.ActionContext.findValueInScopes(\"")
	//						.append(parameter)
	//						.append("\", new org.dihedron.strutlets.annotations.Scope[] {");
	//					boolean first = true;
	//					for(Scope scope : in.scopes()) {
	//						code
	//							.append(first ? "" : ", ")
	//							.append("org.dihedron.strutlets.annotations.Scope.")
	//							.append(scope);
	//						first = false;
	//					}
	//					code.append(" });\n");
						code.append("\tjava.lang.String arg").append(i).append(" = ").append("\"value of '").append(in.value()).append("'\";\n");
					}
				}
				args.append(args.length() > 0 ? ", arg" : "arg").append(i);
			}
			code
				.append("\tjava.lang.String result = ((")
				.append(action.getCanonicalName())
				.append(")$1).")
				.append(method.getName())
				.append("(")
				.append(args)
				.append(");\n");
			code.append("\treturn result;\n").append("}");
		
			logger.trace("compiling code:\n{}'", code);
		
			CtMethod proxyMethod = CtNewMethod.make(code.toString(), proxyClass);
			proxyClass.addMethod(proxyMethod);
			proxyClass.freeze();
		} catch (CannotCompileException e) {
			logger.error("error compiling AOP code in method creation", e);
			throw new StrutletsException("error compiling AOP code in method creation", e);
		}
	}
	
	private String makeProxyClassName(Class<?> clazz) {
		return clazz.getPackage().getName() + ".deploy.$$" + clazz.getSimpleName() + "Stub";
	}
	
	private String makeProxyMethodName(Method method) {
		return method.getName() + "_Proxy";
	}
	
}
