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
public final class WindowState extends javax.portlet.WindowState {
	
	/**
	 * The default window state.
	 */
	public static final WindowState NORMAL = new WindowState("default");
	 
	/**
	 * The minimised window state: only the toolbar is visible.
	 */
	public static final WindowState MINIMISED = new WindowState("minimized");
	
	/**
	 * The maximised (full page) window state.
	 */
	public static final WindowState MAXIMISED = new WindowState("maximized");
	
	/**
	 * The window is shown in a pop-up.
	 */
	public static final WindowState POP_UP = new WindowState("pop_up");
	
	/**
	 * The result is not enriched with the portal's contents: the raw HTML is returned.
	 * This approach is good for AJAX requests.
	 */
	public static final WindowState EXCLUSIVE = new WindowState("exclusive");
	
	/**
	 * The conventional window state to indicate that the window appearance 
	 * should stay the same as in the previous request
	 */
	public static final WindowState SAME = new WindowState("same");
	
	/**
	 * Factory method: returns one of the supported <code>WindowState</code>
	 * objects or a brand new one.
	 *  
	 * @param id
	 *   the window state object.
	 * @return
	 *   one of the supported <code>WindowState</code> objects or a brand new 
	 *   one.
	 */
	public static WindowState fromString(String id) {
		assert(id != null);
		if(SAME.equals(id)) {
			return SAME;
		} else if(NORMAL.equals(id)) {
			return NORMAL;
		} else if(MAXIMISED.equals(id)) {
			return MAXIMISED;
		} else if(MINIMISED.equals(id)) {
			return MINIMISED;
		} else if(EXCLUSIVE.equals(id)) {
			return EXCLUSIVE;
		} else if(POP_UP.equals(id)) {
			return POP_UP;
		}
		return new WindowState(id);
	}
	
	/**
	 * Checks whether the <code>WindowState</code> id is among the supported ones.
	 * 
	 * @param id
	 *   the window state id.
	 * @return
	 *   <code>true</code> if the input window state id is among the supported 
	 *   values, false otherwise.
	 */
	public static boolean isSupported(String id) {
		if(id != null) {
			return (SAME.equals(id) || NORMAL.equals(id) || MAXIMISED.equals(id) || MINIMISED.equals(id) || EXCLUSIVE.equals(id) || POP_UP.equals(id));		
		}
		return false;
	}

	/**
	 * Checks whether the <code>WindowState</code> is among the supported ones.
	 * 
	 * @param state
	 *   the window state.
	 * @return
	 *   <code>true</code> if the input window state is among the supported 
	 *   values, false otherwise.
	 */
	public static boolean isSupported(WindowState state) {
		return isSupported(state.toString());		
	}
	
	/**
	 * Checks if this object is the same as the given one: in order for this to
	 * succeed, the other object must be an instance of @{code WindowState} or
	 * a {@code String} representing the same window state (case insensitively).
	 *  
	 * @param state
	 *   the other object.
	 * @return
	 *   <code>true</code> if the two correspond to the same {@code WindowState}.
	 * @see javax.portlet.PortletMode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object state) {
		return 	
			state != null && 
			(state instanceof WindowState || state instanceof String) && 
			state.toString().equalsIgnoreCase(this.toString());
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
	 * Constructor.
	 * 
	 * @param id
	 *   the window state id.
	 */
	private WindowState(String id) {
		super(id);
	}	
}
