package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the premium verification failed
 *
 */
public class VerifiedPremiumException extends Exception{

	private static final long serialVersionUID = 1L;

	public VerifiedPremiumException(String msg){
		super(msg);
	}

}
