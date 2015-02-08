/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 


package org.dihedron.demo.portlets.portlet6.actions;

import java.io.Serializable;


/**
 * @author Andrea Funto'
 */
public class MyTestBean implements Serializable {


	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 3451081235290951419L;

	private String value1;
	
	private int value2;
	
	/**
	 * Constructor.
	 * 
	 * @param value1
	 *   the string value in the bean.
	 * @param value2
	 *   the integer value in the bean.
	 */
	public MyTestBean(String value1, int value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * Returns the value of value1.
	 *
	 * @return 
	 *   return the value of value1.
	 */
	public String getValue1() {
		return value1;
	}

	/**
	 * Sets a new value for value1.
	 *
	 * @param value1 
	 *   the new value of value1.
	 */
	public void setValue1(String value1) {
		this.value1 = value1;
	}

	/**
	 * Returns the value of value2.
	 *
	 * @return 
	 *   return the value of value2.
	 */
	public int getValue2() {
		return value2;
	}

	/**
	 * Sets a new value for value2.
	 *
	 * @param value2 
	 *   the new value of value2.
	 */
	public void setValue2(int value2) {
		this.value2 = value2;
	}
}
