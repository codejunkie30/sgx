package com.wmsi.sgx.model.validation.annotations;

import static org.testng.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ValidFieldNameValidatorTest{

	private Validator validator;

	@BeforeClass
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void testValidField() {
		ValidateTest t = new ValidateTest();
		t.field = "myFieldName";
		
		Set<ConstraintViolation<ValidateTest>> violations = validator.validate(t);
		assertEquals(violations.size(), 0);		
	}

	@Test
	public void testInvalidField() {
		ValidateTest t = new ValidateTest();
		t.field = "someNonExistentFieldName";
		
		Set<ConstraintViolation<ValidateTest>> violations = validator.validate(t);
		assertEquals(violations.size(), 1);		
	}

	class ValidateTest{
		@ValidFieldName(model=ValidModel.class)
		private String field;
	}
	
	class ValidModel{
		private Integer myFieldName;
		public Integer getMyFieldName(){ return myFieldName;}
	}
}
