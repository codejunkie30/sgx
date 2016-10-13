package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the user verification fails.
 *
 */
public class UserVerificationException extends Exception{

	private static final long serialVersionUID = 1L;

	public UserVerificationException(String msg){
		super(msg);
	}

}
