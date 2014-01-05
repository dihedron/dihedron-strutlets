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

package org.dihedron.demo.portlets.portlet1.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.dihedron.commons.strings.Strings;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.dihedron.strutlets.exceptions.InvalidPhaseException;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action
public class ProxiedAction {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProxiedAction.class);
	

	@Invocable (
		idempotent = true,
		results = {
			@Result(value = Action.SUCCESS, renderer = "jsp", data = "/html/portlet1/view.jsp")	
		}
	)
	public String render(
			@Out(value="friendsAttribute", scope = Scope.REQUEST) $<Set<List<Map<String, Vector<String>>>>> friends,
			@Out(value="descriptionAttribute", scope = Scope.PORTLET) $<String> description,
			@Out(value="ageAttribute", scope = Scope.APPLICATION) $<Integer> age,			
			@Out(value="genderAttribute", scope = Scope.APPLICATION) $<Boolean> gender
		) {
		logger.debug("initialising view, storing parameters into session");
		friends.set(new HashSet<List<Map<String, Vector<String>>>>());
		description.set("a very good person");
		gender.set(true);
		age.set(100);
		return Action.SUCCESS;
	}
	
	
	//
	// By uncommenting this method, the portlet will become undeployable because
	// the framework will detect the method overloading, which is not admitted.
	// You can override, but you MUST NO overload (as there would be no way to 
	// decide which method to invoke when resolving a target such as 
	//              MyAction!myMethod
	// if there are two methods sharing the same name!
//	@Invocable
//	public String render(@In(value="test") String string) {
//		return Action.SUCCESS;
//	}
		
	@Invocable(
		idempotent = true,
		results = {
			@Result(value="success", renderer="jsp", data="/html/portlet1/result.jsp"),
			@Result(value="invalid_input", renderer="jsp", data="/html/portlet1/view_on_input_errors.jsp"),
			@Result(value="redirect_to_homepage", renderer="redirect", data="/web/guest/welcome"),
			@Result(value="redirect_to_google", renderer="redirect", data="http://www.google.co.uk"),
			@Result(value="redirect_to_jsp", renderer="redirect", data="${portlet-context}/html/portlet1/redirect.jsp")
		},
		validator = Portlet1ValidationHandler.class
	)
	@Pattern(regexp="^sucCess$|^error$")
	public String dumpInputs(
			@In(value = "nameParameter", scopes = Scope.FORM) @Size(min=3, max=20, message="error-name-key") String name, 
			@In(value = "surnameParameter", scopes = Scope.FORM) @Size(min=3, max=20, message="error-surname-key") String surname,
			@In(value = "phoneParameter", scopes = Scope.FORM) @Pattern(regexp="^\\d{2}-\\d{3}-\\d{5}$", message="error-phone-key") String phone,
			@In(value = "emailParameter", scopes = Scope.FORM) @Email(message="error-email-key") String email,
			@In(value = "lovesCheckbox", scopes = Scope.FORM) String[] loves,
			@In(value = "redirect", scopes = Scope.FORM) String redirect,
			@In(value="friendsAttribute", scopes = Scope.REQUEST) Set<List<Map<String, Vector<String>>>> friends,
			@In(value="descriptionAttribute", scopes = Scope.PORTLET) String description,
			@In(value="ageAttribute", scopes = Scope.APPLICATION) @Min(10) @Max(120) Integer age,			
			@In(value="genderAttribute", scopes = Scope.APPLICATION) Boolean gender,
			String aString,
			double aDouble,
			@Out(value = "result", scope = Scope.RENDER) $<String> result   
	) throws InvalidPhaseException {
		logger.debug("dumping input parameters from session & form");
		StringBuilder buffer = new StringBuilder("{\n");
		buffer.append("\t'name' : '").append(name).append("' (").append(name.getClass().getName()).append("),\n");
		buffer.append("\t'surname' : '").append(surname).append("' (").append(surname.getClass().getName()).append("),\n");
		buffer.append("\t'phone' : '").append(phone).append("' (").append(phone.getClass().getName()).append("),\n");
		buffer.append("\t'email' : '").append(email).append("' (").append(email.getClass().getName()).append("),\n");		
		buffer.append("\t'loves' : [").append(Strings.join(", ", (Object[])loves)).append("] (").append(loves.getClass().getName()).append("),\n");
		buffer.append("\t'friends' : '").append(friends).append("' (").append(friends.getClass().getName()).append("),\n");
		buffer.append("\t'description' : '").append(description).append("' (").append(description.getClass().getName()).append("),\n");
		buffer.append("\t'age' : '").append(age).append("' (").append(age.getClass().getName()).append("),\n");
		buffer.append("\t'gender' : '").append(gender).append("' (").append(gender.getClass().getName()).append("),\n");
		buffer.append("}");
		
		// you can decide if you want the portlet to redirect to another page 
		// (external, internal to the portal, or another JSP in this portlet 
		// project, or you prefer the portlet to go oin processing the input form
		if(redirect.equalsIgnoreCase("absolute")) {
			return "redirect_to_google";
		} else if(redirect.equalsIgnoreCase("homepage")) {
			return "redirect_to_homepage";
		} else if(redirect.equalsIgnoreCase("internal")) {
			return "redirect_to_jsp";
		} else {
			result.set(buffer.toString());
		}		
		return Action.SUCCESS;
	}	
}
