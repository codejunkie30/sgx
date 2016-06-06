package com.wmsi.sgx.model.validation.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import net.sf.ehcache.hibernate.management.impl.BeanUtils;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object>{

	private PasswordMatch passwordMatchAnnotation;
	
	@Override
	public void initialize(PasswordMatch constraintAnnotation) {
		passwordMatchAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext context) {

		Object password = BeanUtils.getBeanProperty(obj, passwordMatchAnnotation.passwordField());
		Object match = BeanUtils.getBeanProperty(obj, passwordMatchAnnotation.matchField());
		
		// Allows for null passwords to match to defer to other annotations for preventing nulls  ie @NotNull
		return password == null && match == null || password != null && password.equals(match);
	}
}
