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

package org.dihedron.demo.portlets.portlet5.actions;

import org.dihedron.strutlets.annotations.Action;
import org.dihedron.strutlets.annotations.In;
import org.dihedron.strutlets.annotations.Invocable;
import org.dihedron.strutlets.annotations.Out;
import org.dihedron.strutlets.annotations.Result;
import org.dihedron.strutlets.annotations.Scope;
import org.dihedron.strutlets.aop.$;
import org.dihedron.strutlets.exceptions.ActionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action(alias = "InOutAction")
public class InOut {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = LoggerFactory.getLogger(InOut.class);

//	@In( scopes = Scope.FORM ) private String input1;
//	@In( scopes = Scope.FORM ) private String input2;
//	@In( scopes = Scope.FORM ) private String input3;
//	
//	@Out ( scope = Scope.RENDER ) private String output1;
//	@Out ( scope = Scope.REQUEST ) private String output2;
//
//	@In( scopes = Scope.REQUEST )	@Out( scope = Scope.REQUEST ) String requestAttribute;	
//	@In( scopes = Scope.PORTLET ) @Out( scope = Scope.PORTLET ) String portletAttribute;
//	@In( scopes = Scope.APPLICATION ) @Out( scope = Scope.APPLICATION ) String applicationAttribute;
	
	
	@Invocable (
			idempotent = true,
//			inputs = { 
//				"input1", "input2", "input3" 
//			}, 
			results = {
				@Result(value = "success", data = "/html/portlet5/result.jsp")
			}
		)
		public String testInputOutput(
			@In("input1") String input1,
			@In("input2") String input2,
			@In("input3") String input3,
			@Out(value = "output1", scope = Scope.RENDER) $<String> output1,
			@Out(value = "output2", scope = Scope.REQUEST) $<String> output2,
			@Out(value = "portletAttribute", scope = Scope.PORTLET ) $<String> portletAttribute
		) throws ActionException {		
			logger.info("parameter input1 i2 '{}'", input1);		
			logger.info("parameter input1 i2 '{}'", input2);
			logger.info("parameter input1 i2 '{}'", input3);
			output1.set("{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }");
			output2.set("{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }");
			logger.info("output parameter set to '{}'", output1);
			
			portletAttribute.set("ciao");
			return Action.SUCCESS;
		}
	
	/*@Invocable (
		idempotent = true,
		outputs = { "requestAttribute", "portletAttribute", "applicationAttribute" },
		results = {
			@Result(value = AbstractAction.SUCCESS, data = "/html/portlet5/form.jsp")
		}
	)
	public String initForm() throws ActionException {
		requestAttribute = "request attribute value";
		portletAttribute = "portlet attribute value";
		applicationAttribute = "application attribute value";
		logger.trace("request attribute: '{}'", requestAttribute);
		logger.trace("portlet attribute: '{}'", portletAttribute);
		logger.trace("application attribute: '{}'", applicationAttribute);
		return AbstractAction.SUCCESS;
	}
	
	@Invocable (
			idempotent = true,
			inputs = { 
				"input1", "input2", "input3", "requestAttribute", "portletAttribute", "applicationAttribute"
			}, 
			outputs = {
					// FIXME
			},
			results = {
				@Result(value = "success", data = "/html/portlet5/result.jsp")
			}
		)
	public String onFormSubmitted() throws ActionException {
		logger.info("parameter input1 i2 '{}'", input1);		
		logger.info("parameter input1 i2 '{}'", input2);
		logger.info("parameter input1 i2 '{}'", input3);
		output1 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
		output2 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
//			output1 = "{ 'input1' : '" + input1 + "', 'input2' : '" + input2 + "', 'input3' : '" + input3 + "' }";
		
		logger.info("output parameter set to '{}'", output1);
		return AbstractAction.SUCCESS;
	}*/
	
	


//	/* (non-Javadoc)
//	 * @see org.dihedron.portlets.actions.Action#execute()
//	 */
//	@Override
//	@Invocable( inputs = { "result" } )
//	public String execute() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("default method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by default method", result);
//		return AbstractAction.ERROR;
//	}
//	
//	@Invocable(
//		inputs = { "result" },
//		events = {
//			@Event(value="userCreatedEvent", namespace="https://www.dihedron.org/events")	
//		},
//		results = {
//			@Result(value="success", url="/html/portlet1/dynamic/DynamicTestAction/testMethod/success.jsp"),
//			@Result(value="error", url="/html/portlet1/dynamic/DynamicTestAction/testMethod/error.jsp")				
//		}
//	)
//	public String testMethod() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("test method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by test method", result);
//		return AbstractAction.ERROR;
//	}
//
//	@Invocable(
//		inputs = { "result" },
//		semantics=Semantics.READ_ONLY,
//		results = {
//			@Result(value="success", url="/html/portlet1/dynamic/DynamicTestAction/testRenderMethod/success.jsp"),
//			@Result(value="error", url="/html/portlet1/dynamic/DynamicTestAction/testRenderMethod/error.jsp")				
//		}
//	)
//	public String testRenderMethod() throws ActionException {
////		String result = ActionContextImpl.getFirstParameterValue("result");
//		logger.debug("test method is requested to return '{}'", result);
//		if(result.equalsIgnoreCase("success")) {
//			return AbstractAction.SUCCESS;
//		} else if(result.equalsIgnoreCase("error")) {
//			return AbstractAction.ERROR;
//		}
//		logger.warn("result '{}' is unsupported by test method", result);
//		return AbstractAction.ERROR;
//	}	
}
