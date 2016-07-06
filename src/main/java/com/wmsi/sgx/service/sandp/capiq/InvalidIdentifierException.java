package com.wmsi.sgx.service.sandp.capiq;

public class InvalidIdentifierException extends ResponseParserException{

	private static final long serialVersionUID = 1L;

	public InvalidIdentifierException(String msg){
		super(msg);
	}
	
	public InvalidIdentifierException(String msg, Throwable t){
		super(msg, t);
	}
}
