package com.wmsi.sgx.service.gti;

public class GTIServiceException extends Exception{
 
	private static final long serialVersionUID = 1L;

	public GTIServiceException(String msg){
		super(msg);
	}
	
	public GTIServiceException(String msg, Throwable t){
		super(msg, t); 
	}
}
