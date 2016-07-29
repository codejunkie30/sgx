/**
 * 
 */
package com.wmsi.sgx.service;

/**
 * @author dt84327
 *
 */
public class RSAKeyException extends Exception {
	private static final long serialVersionUID = 1L;

	public RSAKeyException(String msg) {
		super(msg);
	}

	public RSAKeyException(String msg, Throwable t) {
		super(msg, t);
	}
}
