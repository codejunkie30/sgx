package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class ApiResponse {
	
	private String message;
	private String messageCode;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("message", message).add("messageCode", messageCode).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(message, messageCode);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof ApiResponse) {
			ApiResponse that = (ApiResponse) object;
			return Objects.equal(this.message, that.message) && Objects.equal(this.messageCode, that.messageCode);
		}
		return false;
	}
	
	

}
