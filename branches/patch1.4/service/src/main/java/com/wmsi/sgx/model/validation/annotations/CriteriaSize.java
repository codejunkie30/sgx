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
@Constraint(validatedBy = CriteriaSizeValidator.class)
public @interface CriteriaSize{

	String message() default "Invalid criteria size";

	int min() default 0;
	int max() default 5;
	
	String[] ignoreFields();
	
	@SuppressWarnings("rawtypes")
	Class[] groups() default {};

	@SuppressWarnings("rawtypes")
	Class[] payload() default {};
}
