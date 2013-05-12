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
package org.dihedron.strutlets.taglib;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.strutlets.ActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class UseBeanTag extends TagSupport {

	/**
	 * An enumeration of the acceptable values for the context where the attribute
	 * or parameter is supposed to be available and will be looked up.
	 *  
	 * @author Andrea Funto'
	 */
	private enum Scope {
		
		/**
		 * The parameter is available as a render parameter (either public or private).
		 */
		RENDER("render"),
		
		/**
		 * The parameter is available to the current portlet on a per-request basis.
		 */
		REQUEST("request"),
		
		/**
		 * The parameter is persistently stored in the current session, and only 
		 * available to the current portlet.
		 */
		PORTLET("portlet"),
		
		/**
		 * The parameter is persistently stored in the current session, and 
		 * available throughout the application to all portlets. 
		 */
		APPLICATION("application");
		
		/**
		 * Constructor.
		 *
		 * @param scope
		 *   the context in which the variable is to be located, as a string.
		 */
		private Scope(String scope) {
			this.scope = scope;
		}
		
		/**
		 * Tries to convert a textual representation into the proper enumeration 
		 * constant.
		 * 
		 * @param text
		 *   the textual representation of the enumeration constant.
		 * @return
		 */
		public static Scope fromString(String text) {
			if (text != null) {
				for (Scope s : Scope.values()) {
					if (text.equalsIgnoreCase(s.scope)) {
						return s;
					}
				}
			}
			throw new IllegalArgumentException("No enumeration value matching '" + text + "'");
		}		
		
		@Override
		public String toString() {
			return scope;
		}
		
		/**
		 * The context, as a string.
		 */
		private String scope;		
	}
	
	/**
	 * The visibility of the new variable: the whole page or only the nested tags.
	 * 
	 * @author Andrea Funto'
	 */
	private enum Visibility {
		
		/**
		 * The variable will be visible from the definition point until the end
		 * of the current JSP page.
		 */
		PAGE("page"),
		
		/**
		 * The variable will only be visible from the start tag until the matching
		 * closing tag.
		 */
		NESTED("nested");
		
		/**
		 * Constructor.
		 *
		 * @param visibility
		 *   the visibility scope of the new variable.
		 */
		private Visibility(String visibility) {
			this.visibility = visibility;
		}
		
		/**
		 * Tries to convert a textual representation into the proper enumeration 
		 * constant.
		 * 
		 * @param text
		 *   the textual representation of the enumeration constant.
		 * @return
		 */
		public static Visibility fromString(String text) {
			if (text != null) {
				for (Visibility v : Visibility.values()) {
					if (text.equalsIgnoreCase(v.visibility)) {
						return v;
					}
				}
			}
			throw new IllegalArgumentException("No enumeration value matching '" + text + "'");
		}		
		
		
		@Override
		public String toString() {
			return visibility;
		}
		
		/**
		 * The visibility scope of the new variable, as a string.
		 */
		private String visibility;
	}
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8197748548084293389L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(UseBeanTag.class);
	
	/**
	 * The default visibility scope of the new variable.
	 */
	private static final Visibility DEFAULT_VISIBILITY = Visibility.PAGE;
	
	/**
	 * The default context where the parameter is supposed to be available and
	 * will be looked up.
	 */
	private static final Scope DEFAULT_SCOPE = Scope.RENDER;
	
	/**
	 * The name of the attribute to be made available to the page and EL. 
	 */
	private String name;
	
	/**
	 * The context in which the attribute/parameter is supposed to be available;
	 * it can have the following values:<ul>
	 * <li>{@code render}: the bean is supposed to be among the render parameters,</li>
	 * <li>{@code request}: the attribute is supposed to be in the request (for
	 * details see {@link ActionContext.Scope#REQUEST},</li>
	 * <li>{@code session}: the attribute is supposed to be in the session (for
	 * details see {@link ActionContext.Scope#SESSION},</li>
	 * <li>{@code application}: the attribute is supposed to be in the application
	 * (for details see {@link ActionContext.Scope#SESSION}.</li>
	 * </ol>
	 */
	private Scope context = DEFAULT_SCOPE;
		
	/**
	 * The name of the destination variable.
	 */
	private String var;
	
	/**
	 * The class (type) of the destination variable.
	 */
	private String type;
	
	/**
	 * The lexical scope of the declared variable; it can have the following values:<ul>
	 * <li>{@code nested}: the variable will be visible only between the start 
	 * and end tags,</li>
	 * <li>{@code page}; the variable will be available from this point until the
	 * end of the page.</li>
	 * </ul>
	 */
	private Visibility visibility = DEFAULT_VISIBILITY;	
	
	/**
	 * Sets the name of the attribute to be made available to the page and EL.
	 * 
	 * @param name
	 *   the name of the attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the context in which the attribute/parameter is supposed to be available
	 * and will be looked up.
	 * 
	 * @param context
	 *   the name of the scope; supported values include:<ul>
	 *   <li>render</li>: one of the render parameters;
	 *   <li>request</li>: the bean is among the request attributes;
	 *   <li>portlet</li>: the bean is among the portlet attributes;
	 *   <li>application</li>: the bean is among the application attributes;
	 * <ul>
	 */
	public void setScope(String context) {
		this.context = Scope.fromString(context);
	}
	
	/**
	 * Sets the name of the destination variable.
	 * 
	 * @param var
	 *   the name of the destination variable.
	 */
	public void setVar(String var) {
		this.var = var;
	}
	
	/**
	 * Sets the type of the destination variable.
	 * 
	 * @param type  
	 *   the type of the destination variable.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets the visibility scope of the new variable.
	 * 
	 * @param visibility
	 *   the visibility scope of the new variable; accepted values include:<ul>
	 *   <li>{@code nested}: the variable will be visible only between the start 
	 *   and end tags,</li>
	 *   <li>{@code page}; the variable will be available from this point until the
	 *   end of the page.</li></ul>
	 */
	public void setVisibility(String visibility) {		
		this.visibility = Visibility.fromString(visibility);
	}	

	@Override
	public int doStartTag() throws JspException {
		Object value = null;
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
		switch(context) {
		case RENDER:
			
			if(type.equals("java.lang.String")) {
				value = request.getParameter(name);
			} else if(type.equals("Ljava.lang.String[")) {
				// FIXME
				value = request.getParameterValues(name);
			}
			break;
		case REQUEST:
			value = getAttribute(ActionContext.ACTION_SCOPED_ATTRIBUTES_KEY, PortletSession.PORTLET_SCOPE);
			if(value != null) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>)value;
				value = map.get(name);
			}
			break;
		case PORTLET:
			value = getAttribute(name, PortletSession.PORTLET_SCOPE);
			break;
		case APPLICATION:
			value = getAttribute(name, PortletSession.APPLICATION_SCOPE);
			break;
		}
				
		pageContext.setAttribute(var, value);
		
		return EVAL_BODY_INCLUDE;
	}
	
	private Object getAttribute(String name, int scope) {
		HttpSession session = pageContext.getSession();
		@SuppressWarnings("unchecked")
		Enumeration<String> names = (Enumeration<String>)session.getAttributeNames();
		while(names.hasMoreElements()) {
			String encodedName = names.nextElement();
			String decodedName = PortletSessionUtil.decodeAttributeName(encodedName);			
			int decodedScope = PortletSessionUtil.decodeScope(name);
			logger.trace("analysing attribute '{}' in scope '{}'", decodedName, decodedScope);
			if(decodedName.equals(name) && decodedScope == scope) {
				logger.trace("attribute '{}' found in PORLTET scope", decodedName);
				return session.getAttribute(encodedName);
			}				
		}
		return null;
	}
}
