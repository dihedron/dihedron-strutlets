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

package org.dihedron.commons.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Funto'
 */
public final class DomHelper {
	
	public static List<Element> getChildrenByTagName(Document document, String name) {
		List<Element> nodes = new ArrayList<Element>();
		for (Node child = document.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getNodeName())) {
				nodes.add((Element) child);
			}
		}
		return nodes;		
	}
	
	public static List<Element> getDescendantsByTagName(Document document, String name) {
		List<Element> nodes = new ArrayList<Element>();
		NodeList children = document.getElementsByTagName(name);
		for(int i = 0; i < children.getLength(); ++i) {
			nodes.add((Element)children.item(i));
		}
		return nodes;		
	}
		
	public static List<Element> getChildrenByTagName(Element parent, String name) {
		List<Element> nodes = new ArrayList<Element>();
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == Node.ELEMENT_NODE && name.equals(child.getNodeName())) {
				nodes.add((Element) child);
			}
		}
		return nodes;
	}
	
	public static List<Element> getDescendantsByTagName(Element parent, String name) {
		List<Element> nodes = new ArrayList<Element>();
		NodeList children = parent.getElementsByTagName(name);
		for(int i = 0; i < children.getLength(); ++i) {
			nodes.add((Element)children.item(i));
		}
		return nodes;		
	}
	

	public static Element getFirstChildByTagName(Element parent, String name) {
		List<Element> nodes = getChildrenByTagName(parent, name);
		if(!nodes.isEmpty()) {
			return nodes.get(0);
		}
		return null;
	}
	
	public static String getElementText(Element element) {
		if(element != null) {
			return element.getTextContent();
		}
		return null;
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private DomHelper() {
	}

}
