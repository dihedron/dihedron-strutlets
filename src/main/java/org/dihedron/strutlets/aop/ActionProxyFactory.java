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
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Scope;
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
	
	public Object getProxyFor(Class<? extends Action> action) throws CannotCompileException, InstantiationException, IllegalAccessException {
		return getProxyClass(action).toClass().newInstance();		
	}
		
	/**
	 * Gets an instance of the generated class proxy.
	 * 
	 * @param action
	 *   the action proxied by the returned class. 
	 * @return
	 *   the proxy class for the given action class.
	 */
	private CtClass getProxyClass(Class<? extends Action> action) {
		CtClass proxy = null;
		String proxyname = makeProxyClassName(action);		
		try {
			logger.trace("trying to retrieve proxy '{}' from class pool...", proxyname);
			proxy = classpool.get(proxyname);
			proxy.defrost();
			logger.trace("... proxy found");
		} catch (NotFoundException e) {
			logger.warn("... proxy not found in class pool, adding");
			classpool.insertClassPath(new ClassClassPath(action));
			proxy = classpool.makeClass(makeProxyClassName(action));
		}
		return proxy;
	}
	
	public void addProxyMethod(Class<? extends Action> action, Method method) throws CannotCompileException {
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
	}
	
	private String makeProxyClassName(Class<?> clazz) {
		return clazz.getPackage().getName() + ".deploy.$$" + clazz.getSimpleName() + "Stub";
	}
	
	private String makeProxyMethodName(Method method) {
		return method.getName() + "_Proxy";
	}
	
}
