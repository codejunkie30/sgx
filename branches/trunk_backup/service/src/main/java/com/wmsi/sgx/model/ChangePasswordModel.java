package com.wmsi.sgx.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.wmsi.sgx.model.validation.annotations.PasswordMatch;
import com.wmsi.sgx.model.validation.annotations.PasswordValid;

@PasswordMatch(passwordField = "password", matchField = "passwordMatch")
public class ChangePasswordModel {

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

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordMatch() {
		return passwordMatch;
	}

	public void setPasswordMatch(String passwordMatch) {
		this.passwordMatch = passwordMatch;
	}

	@Override
	public String toString() {
		return "ChangePasswordModel [email=" + email + ", password=" + password + ", passwordMatch=" + passwordMatch
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordMatch == null) ? 0 : passwordMatch.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangePasswordModel other = (ChangePasswordModel) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordMatch == null) {
			if (other.passwordMatch != null)
				return false;
		} else if (!passwordMatch.equals(other.passwordMatch))
			return false;
		return true;
	}
	
	

}
