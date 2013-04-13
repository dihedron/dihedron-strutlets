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

package org.dihedron.strutlets.actions;


/**
 * @author Andrea Funto'
 */
public final class PortletMode extends javax.portlet.PortletMode {
	
	public static final PortletMode VIEW = new PortletMode("view");
	 
	public static final PortletMode EDIT = new PortletMode("edit");
	
	public static final PortletMode HELP = new PortletMode("help");
	
	public static final PortletMode SAME = new PortletMode("same");

	
	public static PortletMode getPortletMode(String id) {
		assert(id != null);
		if(SAME.equals(id)) {
			return SAME;
		} else if(VIEW.equals(id)) {
			return VIEW;
		} else if(EDIT.equals(id)) {
			return EDIT;
		} else if(HELP.equals(id)) {
			return HELP;
		}
		return new PortletMode(id);
	}
	
	public static boolean isSupported(String id) {
		return (SAME.equals(id) || VIEW.equals(id) || EDIT.equals(id) || HELP.equals(id));	
	}

	public static boolean isSupported(PortletMode mode) {
		assert(mode != null);
		return isSupported(mode.toString());	
	}
	
	/**
	 * Checks if this object is the same as the given one: in order for this to
	 * succeed, the other object must be an instance of @{code PortletMode} or
	 * a {@code String} representing the same portlet mode (case insensitively).
	 *  
	 * @see javax.portlet.PortletMode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object mode) {
		return mode != null &&
				(mode instanceof PortletMode || mode instanceof String) &&
				mode.toString().equalsIgnoreCase(this.toString());		
	}
	
	/**
	 * Returns the object's hash code.
	 * 
	 * @see javax.portlet.WindowState#hashCode()
	 */
	@Override
	public int hashCode() {
		return (this.getClass().getName() + this.toString()).hashCode();		
	}	

	/**
	 * Private contructor.
	 * 
	 * @param id
	 *   the portlet mode identifier.
	 */
	private PortletMode(String id) {
		super(id);
	}
}
