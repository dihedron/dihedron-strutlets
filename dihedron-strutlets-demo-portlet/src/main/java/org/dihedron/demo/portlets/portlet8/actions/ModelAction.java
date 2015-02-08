/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.demo.portlets.portlet8.actions;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.dihedron.core.regex.Regex;
import org.dihedron.strutlets.ActionContext;
import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Model;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action
public class ModelAction {
	
	public static class Address {
		/**
		 * Returns the value of street
		 *
		 * @return 
		 *   the value of street.
		 */
		public String getStreet() {
			return street;
		}
		/**
		 * Sets the value of street.
		 *
		 * @param street 
		 *   the value of street to set.
		 */
		public void setStreet(String street) {
			this.street = street;
		}
		/**
		 * Returns the value of number
		 *
		 * @return 
		 *   the value of number.
		 */
		public int getNumber() {
			return number;
		}
		/**
		 * Sets the value of number.
		 *
		 * @param number 
		 *   the value of number to set.
		 */
		public void setNumber(int number) {
			this.number = number;
		}
		/**
		 * Returns the value of zip
		 *
		 * @return 
		 *   the value of zip.
		 */
		public String getZip() {
			return zip;
		}
		/**
		 * Sets the value of zip.
		 *
		 * @param zip 
		 *   the value of zip to set.
		 */
		public void setZip(String zip) {
			this.zip = zip;
		}
		/**
		 * Returns the value of town
		 *
		 * @return 
		 *   the value of town.
		 */
		public String getTown() {
			return town;
		}
		/**
		 * Sets the value of town.
		 *
		 * @param town 
		 *   the value of town to set.
		 */
		public void setTown(String town) {
			this.town = town;
		}
		
		@Size(min=3, max=20, message="error-address-street-key") 
		private String street;
		
		@Min(value=1, message="error-address-number-key") @Max(value=1000, message="error-address-number-key")
		private int number;
		
		@Pattern(regexp="\\d{5}", message="error-address-zip-key")
		private String zip;
		
		@Size(min=1, max=20, message="error-address-town-key")
		private String town;
	}
	
	public static class User {
		/**
		 * Returns the value of name.
		 *
		 * @return 
		 *   the value of name.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Sets the value of name.
		 *
		 * @param name 
		 *   the value of name to set.
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * Returns the value of surname.
		 *
		 * @return 
		 *   the value of surname.
		 */
		public String getSurname() {
			return surname;
		}
		
		/**
		 * Sets the value of surname.
		 *
		 * @param surname 
		 *   the value of surname to set.
		 */
		public void setSurname(String surname) {
			this.surname = surname;
		}
		
		/**
		 * Returns the value of phone.
		 *
		 * @return 
		 *   the value of phone.
		 */
		public String getPhone() {
			return phone;
		}
		
		/**
		 * Sets the value of phone.
		 *
		 * @param phone 
		 *   the value of phone to set.
		 */
		public void setPhone(String phone) {
			this.phone = phone;
		}
		
		/**
		 * Returns the value of email.
		 *
		 * @return 
		 *   the value of email.
		 */
		public String getEmail() {
			return email;
		}
		
		/**
		 * Sets the value of email.
		 *
		 * @param email 
		 *   the value of email to set.
		 */
		public void setEmail(String email) {
			this.email = email;
		}
		
		/**
		 * Returns the value of address
		 *
		 * @return 
		 *   the value of address.
		 */
		public Address getAddress() {
			return address;
		}

		/**
		 * Sets the value of address.
		 *
		 * @param address 
		 *   the value of address to set.
		 */
		public void setAddress(Address address) {
			this.address = address;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			buffer.append("{\n");
			buffer.append("\tname: '").append(name).append("',\n");
			buffer.append("\tsurname: '").append(surname).append("',\n");
			buffer.append("\temail: '").append(email).append("',\n");
			buffer.append("\tphone: '").append(phone).append("',\n");
			buffer.append("\taddress: {\n");
			buffer.append("\t\tstreet: '").append(address.getStreet()).append("',\n");
			buffer.append("\t\tnumber: '").append(address.getNumber()).append("',\n");
			buffer.append("\t\ttown: '").append(address.getTown()).append("',\n");
			buffer.append("\t\tzip: '").append(address.getZip()).append("',\n");
			buffer.append("\t}\n");
			buffer.append("}\n");
			return buffer.toString();
		}
		
		/**
		 * The user's name.
		 */
		@Size(min=3, max=20, message="error-name-key") 
		private String name;
		
		/**
		 * The user's family name.
		 */
		@Size(min=3, max=20, message="error-surname-key")
		private String surname;
		
		/**
		 * The user's email address.
		 */
		@Pattern(regexp="^\\d{2}-\\d{3}-\\d{5}$", message="error-phone-key")
		private String phone;
		
		/**
		 * The user's phone number.
		 */
		@Email(message="error-email-key")
		private String email;
		
		/**
		 * The user's address.
		 */
		@Valid
		private Address address = new Address();
	}

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ModelAction.class);
	

	@Invocable (
		idempotent = true,
		results = {
			@Result(value = Action.SUCCESS, renderer = "jsp", data = "/html/portlet8/view.jsp")	
		}
	)
	public String render() {
		logger.debug("initialising view...");
		User user = new User();
//		user.setName("please enter your name...");
//		user.setSurname("please enter your family name...");
//		user.setEmail("please enter your email address...");
//		user.setPhone("please enter your phone number...");
//		Address address = new Address();
//		address.setStreet("please enter the street where you live...");
//		address.setNumber(0);
//		address.setTown("please enter your town...");
//		address.setZip("please enter your ZIP code...");
//		user.setAddress(address);
		ActionContext.setPortletAttribute("user", user);
		
		return Action.SUCCESS;
	}
	
		
	@Invocable(
		idempotent = true,
		results = {
			@Result(value="success", renderer="jsp", data="/html/portlet8/result.jsp"),
			@Result(value="invalid_input", renderer="jsp", data="/html/portlet8/view_on_input_errors.jsp"),
			@Result(value="redirect_to_homepage", renderer="redirect", data="/web/guest/welcome"),
			@Result(value="redirect_to_google", renderer="redirect", data="http://www.google.co.uk"),
			@Result(value="redirect_to_jsp", renderer="redirect", data="${portlet-context}/html/portlet8/redirect.jsp")			
		},
		validator = Portlet8ValidationHandler.class
	)
	@Pattern(regexp="^success$|^error$")
	public String processUser(
//			@Model(value = "^user\\:(.*)$") @Out(value = "user", to = Scope.PORTLET) $<User> user, 
			@Model @Out(value = "user", to = Scope.PORTLET) $<User> user,
			@Out(value = "result", to = Scope.RENDER) $<String> result) {
		
//		// this fires a null pointer exception!
//		Object boom = null;
//		boom.toString();
		
		logger.debug("processing user model object...");	
		result.set(user.toString());		
		logger.debug("user model object: \n{}", user.toString());
				
		return Action.SUCCESS;
	}

	private static void test(Regex regex, String string) {
		regex.matches(string);
		List<String[]> groups = regex.getAllMatches(string);
		for(String[] matches : groups) {
			logger.trace("-----------------------------------------------------");
			for(String match : matches) {
				logger.trace("match: '{}'", match);
			}
		}
		logger.trace("-----------------------------------------------------");		
		
	}
	
	public static void main(String [] args) {		
	
//		Regex regex = new Regex("^(?:user)\\:(.*)$");
//		Regex regex = new Regex("(?:user)\\:(.*)");
//		Regex regex = new Regex("user\\:(.*)");
		Regex regex = new Regex("^(?:[^\\:]*)\\:(.+)$");
		test(regex, "user:name");
		test(regex, "user:surname");
		test(regex, "user:address.street");
		test(regex, "user:address.street.number");
		test(regex, "users:name");
	}
}
