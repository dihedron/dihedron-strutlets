package org.dihedron.strutlets.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javassist.Modifier;

import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.exceptions.StrutletsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Action
public class MyExtendingAction extends MyBaseAction {
	
	private static final Logger logger = LoggerFactory.getLogger(MyExtendingAction.class); 
	
	private String data = "extending_data";
	
	@Invocable
	public String myInvocableMethod1(@In("ke1") String arg1, @In("key2") String arg2) {
		logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
	
	@Invocable
	public String myInvocableMethod3(@In("key1") String arg1, @In("key2") String arg2, Integer arg3, 
			boolean arg4, char arg5, byte arg6, short arg7, int arg8, long arg9, float arg10, double arg11) {
		logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
	
	public String myNonInvocableMethod4() {
		return Action.SUCCESS;
	}
	
	/**
	 * @param args
	 * @throws StrutletsException 
	 */
	public static void main(String[] args) throws Exception {
		ActionProxyFactory instrumentor = new ActionProxyFactory();
		
		ActionProxy proxy = instrumentor.makeActionProxy(MyExtendingAction.class, true);
		Map<Method, Method> methods = proxy.getMethods();
		for(Entry<Method, Method> entry : methods.entrySet()) {
			Method actionMethod = entry.getKey();
			Method proxyMethod = entry.getValue();
			logger.trace("method '{}' in class '{}' is proxied by method '{}' in class '{}' ({} {})", actionMethod.getName(),
					actionMethod.getDeclaringClass().getSimpleName(), proxyMethod.getName(),
					proxyMethod.getDeclaringClass().getSimpleName(), 
					Modifier.isStatic(proxyMethod.getModifiers()) ? "static" : "non-static",
					Modifier.isFinal(proxyMethod.getModifiers()) ? "final" : "non-final");
			if(actionMethod.getName().equals("execute")) {
				MyBaseAction actionInstance = MyExtendingAction.class.newInstance();
				String result = (String)proxyMethod.invoke(null, actionInstance);
				logger.trace("result is '{}'", result);
			}
		}
	}
		
		
		
//		instrumentor.openClass(MyExtendingAction.class);
//		for(Method method : MyExtendingAction.class.getDeclaredMethods()) {
//			if(!Modifier.isStatic(method.getModifiers())) {
//				instrumentor.addMethod(method);
//			}
//		}
//		instrumentor.sealClass();
//		Class<?> clazz = instrumentor.getProxyClass();
//		for(Method method : MyExtendingAction.class.getDeclaredMethods()) {
//			if(!Modifier.isStatic(method.getModifiers())) {
//				Method meth = instrumentor.getProxyMethod(method);
//			}
//		}
	
	
}