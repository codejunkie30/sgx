package com.wmsi.sgx.service.sandp.capiq;

public class CapIQServiceException extends Exception{

	private static final long serialVersionUID = 1L;

	public CapIQServiceException(String msg){
		super(msg);
	}
	
	public CapIQServiceException(String msg, Throwable t){
		super(msg, t);
	}
}
