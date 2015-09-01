package com.wmsi.sgx.controller.error;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "error")
public class ErrorResponse extends HashMap<String, Object>{

	private static final long serialVersionUID = 1L;

	public ErrorResponse(ErrorMessage m){
		/*int startIndexOfFirstError = m.getMessage().indexOf('[');
		String validationErrorString = m.getMessage().substring(startIndexOfFirstError); 
		
		if(validationErrorString.contains("[")){
			String[] errorMessages = m.getMessage().split("\\[");
			
			for( String msg: errorMessages){
				m.getErrorList().add(msg.replace("[", "").replace("]", ""));
			}
		}else{
			m.getErrorList().add(m.getMessage()+":"+ m.getErrorCode());
		}*/
		
		put("details",m);		
	}
}
