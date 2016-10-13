package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the user verification failed.
 *
 */
public class VerifiedUserException extends Exception{
	private static final long serialVersionUID = 1L;

	public VerifiedUserException(String msg) {
		super(msg);
		
	}

	
}
