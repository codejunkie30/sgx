package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the token is invalid
 *
 */
public class InvalidTokenException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String msg){
		super(msg);
	}
}
