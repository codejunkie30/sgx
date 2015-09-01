package com.wmsi.sgx.controller.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessage{

	private String message;
	private Integer internalCode;
	private List<String> errorList;

	

	public ErrorMessage(){}
	
	public ErrorMessage(String msg, Integer code){
		message = msg;
		internalCode = code;
		errorList = new ArrayList<String>();
		
		int startIndexOfFirstError = msg.indexOf('[');
		if(startIndexOfFirstError>0){
			String validationErrorString = msg.substring(startIndexOfFirstError); 
			System.out.println(validationErrorString);
			if(validationErrorString.contains("[")){
				String[] errorMessages = validationErrorString.split("\\[");
				
				for( String message: errorMessages){
					System.out.println(message);
					if(message.length()>0)
					errorList.add(message.replace("[", "").replace("]", ""));
				}
			}else{
				errorList.add(msg.replace("[", "").replace("]", ""));
			}
		}	
	}
	
	public String getMessage(){return message;}
	public Integer getErrorCode(){return internalCode;}
	public List<String> getErrorList() {return errorList;}
}
