/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.strutlets.aop;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.executable.ExecutableValidator;
import javax.validation.groups.Default;

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
			logger.info("violation on value {}: {}", violation.getInvalidValue(), violation.getMessage());
		}
		violations = validator.validateReturnValue(bean, method, "succEss", Default.class);
		for(ConstraintViolation<ValidatedBean> violation : violations) {
			logger.info("violation on value {}: {}", violation.getInvalidValue(), violation.getMessage());
		}
		
	}
}
