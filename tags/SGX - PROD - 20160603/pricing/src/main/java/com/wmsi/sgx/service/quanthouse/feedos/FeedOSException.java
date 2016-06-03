package com.wmsi.sgx.service.quanthouse.feedos;

public class FeedOSException extends Exception {

	private static final long serialVersionUID = 1L;

	public FeedOSException(String message) {
        super(message);
    }

    public FeedOSException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
