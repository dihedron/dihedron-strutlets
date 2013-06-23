package org.dihedron.strutlets.aop;

import org.dihedron.strutlets.actions.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBaseAction extends Action {
	
	private static final Logger logger = LoggerFactory.getLogger(MyBaseAction.class); 
	
	private String data = "base_data";
	
	@Invocable
	public String myInvocableMethod1(@In("key1") String arg1, @In("key2") String arg2) {
		logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
	
	@Invocable
	public String myInvocableMethod2(@In("key1") String arg1, @In("key2") String arg2) {
		logger.info("arg1 is '{}', arg2 is '{}', data is '{}'", arg1, arg2, data);
		return Action.SUCCESS;
	}
}