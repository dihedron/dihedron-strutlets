package org.dihedron.strutlets.aop;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;

public class MyAction extends Action {
	
	private String data = "paperino";
	
	public String myMethod1(@In("pippo") String arg1, @In("pluto") String arg2) {
		ProxyFactoryTest.logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
	
	public String myMethod2(@In("pippo") String arg1, @In("pluto") String arg2) {
		ProxyFactoryTest.logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
	
}