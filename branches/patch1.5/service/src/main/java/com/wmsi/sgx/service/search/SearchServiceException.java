package com.wmsi.sgx.service.search;

public class SearchServiceException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public SearchServiceException(String msg){
		super(msg);
	}

	public SearchServiceException(String msg, Throwable t){
		super(msg, t);
	}
}
