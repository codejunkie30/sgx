package com.wmsi.sgx.service.quanthouse;


public class InvalidInstrumentException extends QuanthouseServiceException {

	private static final long serialVersionUID = 1L;

	public InvalidInstrumentException(String message) {
		super(message);
	}
}
