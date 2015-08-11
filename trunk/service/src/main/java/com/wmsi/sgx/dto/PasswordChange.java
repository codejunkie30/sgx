package com.wmsi.sgx.dto;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.validation.annotations.PasswordMatch;
import com.wmsi.sgx.model.validation.annotations.PasswordValid;

@PasswordMatch(passwordField = "password", matchField = "passwordMatch")
public class PasswordChange{

	@PasswordValid
	private String password;
	
	private String passwordMatch;

	public String getPassword() {
		return password;
	}

	public String getPasswordMatch() {
		return passwordMatch;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordMatch(String passwordMatch) {
		this.passwordMatch = passwordMatch;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("password", password).add("passwordMatch", passwordMatch).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(password, passwordMatch);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof PasswordChange){
			PasswordChange that = (PasswordChange) object;
			return Objects.equal(this.password, that.password) && Objects.equal(this.passwordMatch, that.passwordMatch);
		}
		return false;
	}

}
