package com.wmsi.sgx.controller.error;

public class ErrorMessage{

	private String message;
	private Integer internalCode;

	public ErrorMessage(){}
	
	public ErrorMessage(String msg, Integer code){
		message = msg;
		internalCode = code;
	}
	
	public String getMessage(){return message;}
	public Integer getErrorCode(){return internalCode;}
}
