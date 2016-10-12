package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the user doesn't exists
 */
public class UserExistsException extends Exception{

	private static final long serialVersionUID = 1L;

	public UserExistsException(String msg){
		super(msg);
	}
}
