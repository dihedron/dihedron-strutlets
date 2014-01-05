/**
 * Copyright (c) 2012-2014, Andrea Funto'. All rights reserved.
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
