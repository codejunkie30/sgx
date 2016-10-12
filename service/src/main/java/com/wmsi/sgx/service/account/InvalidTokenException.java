package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs when there is invalid token deom the
 * UI.
 *
 */
public class InvalidTokenException extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidTokenException(String msg){
		super(msg);
	}
}
