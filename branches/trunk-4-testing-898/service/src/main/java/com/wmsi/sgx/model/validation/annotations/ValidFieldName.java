package com.wmsi.sgx.model.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidFieldNameValidator.class)
public @interface ValidFieldName{

	String message() default "Invalid field name";

	Class<?> model();

	@SuppressWarnings("rawtypes")
	Class[] groups() default {};

	@SuppressWarnings("rawtypes")
	Class[] payload() default {};
}
