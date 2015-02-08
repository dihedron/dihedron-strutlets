/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.strutlets.actions;


/**
 * @author Andrea Funto'
 */
public final class PortletMode extends javax.portlet.PortletMode {
	
	/**
	 * The VIEW portlet mode: this is the normal portlet mode.
	 */
	public static final PortletMode VIEW = new PortletMode("view");
	 
	/**
	 * The EDIT mode, in which the portlet would normally allow user input.
	 */
	public static final PortletMode EDIT = new PortletMode("edit");
	
	/**
	 * The HELP portlet mode, where inline help would normally be served.
	 */
	public static final PortletMode HELP = new PortletMode("help");
	
	/**
	 * A constant indicating that the portlet mode should not change from what it is.
	 */
	public static final PortletMode SAME = new PortletMode("same");

	/**
	 * Factory method: returns the portlet mode corresponding to the given id.
	 * 
	 * @param id
	 *   the string representation of the portlet mode.
	 * @return
	 *   the corresponding portlet mode from the static instances if supported, 
	 *   or a brand new mode otherwise.
	 */
	public static PortletMode fromString(String id) {
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
	
	/**
	 * Returns whether the given string represents one of the supported modes.
	 * 
	 * @param id
	 *   the portlet mode, as a string.
	 * @return
	 *   <code>true</code> if supported, <code>false</code> otherwise.
	 */
	public static boolean isSupported(String id) {
		return (SAME.equals(id) || VIEW.equals(id) || EDIT.equals(id) || HELP.equals(id));	
	}

	/**
	 * Returns whether the given portlet mode is one of the supported modes.
	 * 
	 * @param id
	 *   the portlet mode, as a string.
	 * @return
	 *   <code>true</code> if supported, <code>false</code> otherwise.
	 */
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
