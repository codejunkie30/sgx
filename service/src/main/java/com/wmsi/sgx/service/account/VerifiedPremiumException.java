package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs during verification of the Premium
 * user.
 *
 */
public class VerifiedPremiumException extends Exception{

	private static final long serialVersionUID = 1L;

	public VerifiedPremiumException(String msg){
		super(msg);
	}

}
