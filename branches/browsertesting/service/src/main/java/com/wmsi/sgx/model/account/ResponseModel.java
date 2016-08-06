package com.wmsi.sgx.model.account;

import com.google.common.base.Objects;

public class ResponseModel {
	
	private String message;
	private int responseCode;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("message", message).add("responseCode", responseCode).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(message, responseCode);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof ResponseModel) {
			ResponseModel that = (ResponseModel) object;
			return Objects.equal(this.message, that.message) && Objects.equal(this.responseCode, that.responseCode);
		}
		return false;
	}
	
	

}
