package com.wmsi.sgx.conversion.dozer;

public class ConverterException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private String resultError;

	public ConverterException(String msg){
		super(msg);
	}
	
	public ConverterException(String msg, Throwable t){
		super(msg, t);
	}

	public ConverterException(String msg, String resultError){
		super(msg);
	}

	public String getResultError() {
		return resultError;
	}

	public void setResultError(String resultError) {
		this.resultError = resultError;
	}
}
