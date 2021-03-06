package com.wmsi.sgx.model.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface PasswordValid { 
	//hard coded as its not picking up value from message.properties
    String message() default "Password does not meet criteria";
    
    Class<?>[] groups() default {}; 
    
    Class<? extends Payload>[] payload() default {};
    
    int minLength() default 8;
    
    int maxLength() default 40;
}