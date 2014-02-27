package com.wmsi.sgx.service.sandp.capiq;

public class CapIQRequestException extends Exception{

	private static final long serialVersionUID = 1L;

	public CapIQRequestException(String msg){
		super(msg);
	}

	public CapIQRequestException(String msg, Throwable e){
		super(msg, e);
	}
}
