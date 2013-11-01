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
package org.dihedron.strutlets.aop;

import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.RegEx;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.executable.ExecutableValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author andrea
 */
public class ValidatedBean {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ValidatedBean.class);

	/**
	 * @param name
	 * @param surname
	 * @param age
	 * @return
	 */
	@NotNull
	@Pattern(regexp="^success$|^error$", message="not in the valid set of results")
	public String myMethod(@NotNull String name, @NotNull String surname, @Min(20) @Max(30) int age) {
		return "name" + " " + surname + ", aged " + age;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		ExecutableValidator validator = factory.getValidator().forExecutables();
		ValidatedBean bean = new ValidatedBean();
		Method method = ValidatedBean.class.getMethod("myMethod", String.class, String.class, int.class);
		Object[] values = { "Andrea", "Funtò", 18};
		Set<ConstraintViolation<ValidatedBean>> violations = validator.validateParameters(bean, method, values);
		for(ConstraintViolation<ValidatedBean> violation : violations) {
			System.out.println("violation on value " + violation.getInvalidValue() + ": " + violation.getMessage());
		}
		violations = validator.validateReturnValue(bean, method, "succEss");
		for(ConstraintViolation<ValidatedBean> violation : violations) {
			System.out.println("violation on value " + violation.getInvalidValue() + ": " + violation.getMessage());
		}
		
	}
}
