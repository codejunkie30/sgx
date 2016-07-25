package com.wmsi.sgx.service;

public class CompanyServiceException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public CompanyServiceException(String msg){
		super(msg);
	}

	public CompanyServiceException(String msg, Throwable t){
		super(msg, t);
	}
	
}
