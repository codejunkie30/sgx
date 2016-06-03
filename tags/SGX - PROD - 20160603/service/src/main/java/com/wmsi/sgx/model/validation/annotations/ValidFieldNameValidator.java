package com.wmsi.sgx.model.validation.annotations;

import java.lang.reflect.Field;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class ValidFieldNameValidator implements ConstraintValidator<ValidFieldName, String> {

	private static final Logger log = LoggerFactory.getLogger(ValidFieldNameValidator.class);

	private Class<?> clz;
	
	@Override
	public void initialize(ValidFieldName annot) {
		clz = annot.model();
	}

	@Override
	public boolean isValid(String field, ConstraintValidatorContext ctx) {
		Field f = ReflectionUtils.findField(clz, field);
		log.debug("Field name '{}' valid: {}", field, f != null);
		return f != null;
	}	 

}
