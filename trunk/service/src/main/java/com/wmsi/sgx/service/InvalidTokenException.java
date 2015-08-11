package com.wmsi.sgx.service;

public class InvalidTokenException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String msg){
		super(msg);
	}
}
