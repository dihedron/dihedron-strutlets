/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
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

import org.dihedron.core.strings.Strings;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.InOut;
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
			@Out(value="friendsAttribute", to = Scope.REQUEST) $<Set<List<Map<String, Vector<String>>>>> friends,
			@Out(value="descriptionAttribute", to = Scope.PORTLET) $<String> description,
			@Out(value="ageAttribute", to = Scope.APPLICATION) $<Integer> age,			
			@Out(value="genderAttribute", to = Scope.APPLICATION) $<Boolean> gender
		) {
		logger.debug("initialising view, storing parameters into session");
		friends.set(new HashSet<List<Map<String, Vector<String>>>>());
		description.set("a very good person");
		gender.set(true);
		age.set(100);

		/*
		String boom = null;
		boom.length();
		*/
		
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
			@In(value = "nameParameter", from = Scope.FORM) @Size(min=3, max=20, message="error-name-key") String name, 
			@In(value = "surnameParameter", from = Scope.FORM) @Size(min=3, max=20, message="error-surname-key") String surname,
			@In(value = "phoneParameter", from = Scope.FORM) @Pattern(regexp="^\\d{2}-\\d{3}-\\d{5}$", message="error-phone-key") String phone,
			@In(value = "emailParameter", from = Scope.FORM) @Email(message="error-email-key") String email,
			@In(value = "lovesCheckbox", from = Scope.FORM) String[] loves,
			@In(value = "redirect", from = Scope.FORM) String redirect,
			@In(value="friendsAttribute", from = Scope.REQUEST) Set<List<Map<String, Vector<String>>>> friends,
			@In(value="descriptionAttribute", from = Scope.PORTLET) String description,
			@In(value="ageAttribute", from = Scope.APPLICATION) @Min(10) @Max(120) Integer age,			
			@In(value="genderAttribute", from = Scope.APPLICATION) Boolean gender,
			String aString,
			double aDouble,
			@InOut(value = "result", from = Scope.FORM, to = Scope.RENDER) $<String> result   
	) throws InvalidPhaseException {
		logger.debug("dumping input parameters from session & form");
		StringBuilder buffer = new StringBuilder("{\n");
		buffer.append("\t'name' : '").append(name).append("' (").append(name.getClass().getName()).append("),\n");
		buffer.append("\t'surname' : '").append(surname).append("' (").append(surname.getClass().getName()).append("),\n");
		buffer.append("\t'phone' : '").append(phone).append("' (").append(phone.getClass().getName()).append("),\n");
		buffer.append("\t'email' : '").append(email).append("' (").append(email.getClass().getName()).append("),\n");		
		buffer.append("\t'loves' : [").append(Strings.join(", ", (Object[])loves)).append("] (").append(loves.getClass().getName()).append("),\n");
		if(friends != null) {
			// "friends" will be null because it was set in a REQUEST-scoped parameter 
			// by the "render()" method, yet this method is being invoked in the action-phase
			// and all REQUEST-scoped parameters are erased when a new action phase fires!
			buffer.append("\t'friends' : '").append(friends).append("' (").append(friends.getClass().getName()).append("),\n");
		}
		buffer.append("\t'description' : '").append(description).append("' (").append(description != null ? description.getClass().getName() : "<null>").append("),\n");
		buffer.append("\t'age' : '").append(age).append("' (").append(age != null ? age.getClass().getName() : "<null>").append("),\n");
		buffer.append("\t'gender' : '").append(gender).append("' (").append(gender != null ? gender.getClass().getName() : "<null>").append("),\n");
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
