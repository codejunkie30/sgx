package com.wmsi.sgx.service;

public class AlphaFactorServiceException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public AlphaFactorServiceException(String msg){
		super(msg);
	}

	public AlphaFactorServiceException(String msg, Throwable t){
		super(msg, t);
	}
	
}
