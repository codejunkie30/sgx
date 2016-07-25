package com.wmsi.sgx.security;

public class AuthenticationFailure {
	
	private String reason;
	private String errorCode;

	

	public AuthenticationFailure(String r){
		reason = r;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
