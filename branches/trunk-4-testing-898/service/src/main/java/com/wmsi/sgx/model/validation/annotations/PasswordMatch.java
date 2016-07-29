package com.wmsi.sgx.model.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE}) 
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch { 
	//hard coded as its not picking up value from message.properties
    String message() default "Passwords do not match";
    
    Class<?>[] groups() default {}; 
    
    Class<? extends Payload>[] payload() default {};
    
    String passwordField();
    
    String matchField();
}