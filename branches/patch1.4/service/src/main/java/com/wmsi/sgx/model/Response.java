package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class Response {
	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("message", message)
			.toString();
	}
	
	
}