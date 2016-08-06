package com.wmsi.sgx.service.vwap;

public class VWAPServiceException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public VWAPServiceException(String msg){
		super(msg);
	}
	public VWAPServiceException(String msg, Throwable t){
		super(msg, t);
	}
}