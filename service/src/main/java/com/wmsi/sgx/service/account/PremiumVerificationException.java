package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs during verification of the Premium
 * user.
 *
 */
public class PremiumVerificationException extends Exception{

	private static final long serialVersionUID = 1L;

	public PremiumVerificationException(String msg){
		super(msg);
	}

}
