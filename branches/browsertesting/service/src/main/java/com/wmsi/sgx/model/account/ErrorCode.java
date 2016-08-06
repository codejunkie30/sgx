package com.wmsi.sgx.model.account;

import javax.validation.constraints.NotNull;
import com.google.common.base.Objects;

public class ErrorCode {
	
	@NotNull
	private String errorCode;
	
	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("errorCode", errorCode)
			.toString();
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(errorCode);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof ErrorCode) {
			ErrorCode that = (ErrorCode) object;
			return Objects.equal(this.errorCode, that.errorCode);
		}
		return false;
	}

}
