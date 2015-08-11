package com.wmsi.sgx.dto;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.validation.annotations.PasswordMatch;
import com.wmsi.sgx.model.validation.annotations.PasswordValid;

@PasswordMatch(passwordField = "password", matchField = "passwordMatch")
public class UserDTO{

	@Email
	@NotNull
	private String email;
	
	@NotNull
	@PasswordValid
	private String password;
	
	@NotNull
	private String passwordMatch;

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordMatch() {
		return passwordMatch;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordMatch(String passwordMatch) {
		this.passwordMatch = passwordMatch;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(email, password, passwordMatch);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof UserDTO) {
			UserDTO that = (UserDTO) object;
			return Objects.equal(this.email, that.email)
				&& Objects.equal(this.password, that.password)
				&& Objects.equal(this.passwordMatch, that.passwordMatch);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("email", email)
			.add("password", password)
			.add("passwordMatch", passwordMatch)
			.toString();
	}

}
