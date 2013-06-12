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

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.CannotCompileException;

import org.dihedron.strutlets.actions.Action;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class ProxyFactoryTest {
	/**
	 * The logger.
	 */
	static final Logger logger = LoggerFactory.getLogger(ProxyFactoryTest.class);

	/**
	 * Test method for {@link org.dihedron.strutlets.aop.ActionProxyFactory#addProxyMethod(java.lang.Class, java.lang.reflect.Method)}.
	 * @throws CannotCompileException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws InstantiationException 
	 */
	@Test
//	@Ignore
	public void testMakeProxyMethod() throws CannotCompileException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
		try {
			ActionProxyFactory factory = new ActionProxyFactory();
			Class <? extends Action> action = MyAction.class;
			Method [] methods = action.getDeclaredMethods();
			for(Method method: methods) {
				logger.trace("method: '{}'", method.getName());
				factory.addProxyMethod(action, method);
			}
			
			MyAction testAction = new MyAction();
			
			Object proxy = factory.getProxyFor(MyAction.class);
			
			logger.trace("proxy objct is of class '{}'", proxy.getClass().getCanonicalName());
			
			for(Method method : proxy.getClass().getDeclaredMethods()) {
				logger.trace("method: '{}!{}'", proxy.getClass().getCanonicalName(), method.getName());
				method.invoke(proxy, testAction);
			}
		} catch(Exception e) {
		}
		
		assertTrue(true);
	}
	
	
}
