package com.wmsi.sgx.service.account;

public class QuanthouseServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public QuanthouseServiceException(String message) {
        super(message);
    }

    public QuanthouseServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
