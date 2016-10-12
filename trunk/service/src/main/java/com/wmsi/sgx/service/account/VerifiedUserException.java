package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs during verification of the user.
 *
 */
public class VerifiedUserException extends Exception{
	private static final long serialVersionUID = 1L;

	public VerifiedUserException(String msg) {
		super(msg);
		
	}

	
}
