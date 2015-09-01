package com.wmsi.sgx.controller.error;

import java.util.List;

public class ValidateErrorResponse extends ErrorResponse{

	private static final long serialVersionUID = 1L;
	List<String> errors;
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public ValidateErrorResponse(ErrorMessage m) {
		super(m);
		
		if(m.getMessage().contains("[")){
			String[] errorMessages = m.getMessage().split("[");
			for( String msg: errorMessages){
				errors.add(msg.replace("[", "").replace("]", "")+":"+ m.getErrorCode());
			}
		}else{
			errors.add(m.getMessage()+":"+ m.getErrorCode());
		}
	}
	
	
}
