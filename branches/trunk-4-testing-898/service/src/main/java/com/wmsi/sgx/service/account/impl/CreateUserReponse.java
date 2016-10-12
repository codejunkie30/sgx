package com.wmsi.sgx.service.account.impl;

import com.google.common.base.Objects;

/**
 * This domain class holds the token and user name information
 */
public class CreateUserReponse {
	
	private String token;
	private String username;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(token, username);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof CreateUserReponse) {
			CreateUserReponse that = (CreateUserReponse) object;
			return Objects.equal(this.token, that.token) && Objects.equal(this.username, that.username);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("token", token).add("username", username).toString();
	}
	
	

}
