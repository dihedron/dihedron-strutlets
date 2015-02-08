/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.aop;

import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Action
public class MyBaseAction {
	
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