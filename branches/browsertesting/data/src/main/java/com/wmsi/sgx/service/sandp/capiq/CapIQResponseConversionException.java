package com.wmsi.sgx.service.sandp.capiq;

public class CapIQResponseConversionException extends CapIQRequestException{

	private static final long serialVersionUID = 1L;

	public CapIQResponseConversionException(String msg){
		super(msg);
	}

	public CapIQResponseConversionException(String msg, Throwable t){
		super(msg,t);
	}

}
