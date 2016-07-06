package com.wmsi.sgx.service.sandp.capiq;

public class ResponseParserException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public ResponseParserException(String msg){
		super(msg);
	}
	
	public ResponseParserException(String msg, Throwable t){
		super(msg, t);
	}

}
