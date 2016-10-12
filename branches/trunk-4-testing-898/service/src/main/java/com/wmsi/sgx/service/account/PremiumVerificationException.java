package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that Premium verification failed
 * 
 */
public class PremiumVerificationException extends Exception{

	private static final long serialVersionUID = 1L;

	public PremiumVerificationException(String msg){
		super(msg);
	}

}
