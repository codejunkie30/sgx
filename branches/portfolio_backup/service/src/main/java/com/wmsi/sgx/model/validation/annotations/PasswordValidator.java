package com.wmsi.sgx.model.validation.annotations;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid, String>{

	private final Pattern hasAlphaNumeric = Pattern.compile("\\p{Alnum}", Pattern.UNICODE_CHARACTER_CLASS);
	private final Pattern hasNumber = 		Pattern.compile("\\p{Digit}", Pattern.UNICODE_CHARACTER_CLASS);
	private final Pattern hasSpecialChar = 	Pattern.compile("[^\\p{Alnum} ]", Pattern.UNICODE_CHARACTER_CLASS);
	
	private PasswordValid passwordValidAnnotation;
	
	@Override
	public void initialize(PasswordValid constraintAnnotation) {
		passwordValidAnnotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		
		// Check lengths
		if(password.length() < passwordValidAnnotation.minLength() || 
				password.length() > passwordValidAnnotation.maxLength())		
			return false;
			
		// Check valid patterns
		return 	hasAlphaNumeric.matcher(password).find() && 
				(hasNumber.matcher(password).find() &&
				 hasSpecialChar.matcher(password).find());				
	}

}
