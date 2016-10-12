package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs during verification of the existence
 * of the user.
 *
 */
public class UserExistsException extends Exception{

	private static final long serialVersionUID = 1L;

	public UserExistsException(String msg){
		super(msg);
	}
}
