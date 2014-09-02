package com.wmsi.sgx.service.search.filter;

public class FilterException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public FilterException(String msg){
		super(msg);
	}

	public FilterException(String msg, Throwable t){
		super(msg, t);
	}

}
