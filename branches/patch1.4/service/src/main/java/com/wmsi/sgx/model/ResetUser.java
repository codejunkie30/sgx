package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class ResetUser{

	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("username", username).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(username);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof ResetUser){
			ResetUser that = (ResetUser) object;
			return Objects.equal(this.username, that.username);
		}
		return false;
	}
	
	
}
