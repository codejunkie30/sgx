package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used to generate the QuanthouseServiceException during Quant
 * House Service execution.
 *
 */
public class QuanthouseServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public QuanthouseServiceException(String message) {
        super(message);
    }

    public QuanthouseServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
