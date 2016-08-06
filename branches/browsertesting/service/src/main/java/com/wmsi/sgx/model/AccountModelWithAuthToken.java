package com.wmsi.sgx.model;

import com.wmsi.sgx.model.account.AccountModel;
import com.google.common.base.Objects;

public class AccountModelWithAuthToken extends AccountModel {

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("super", super.toString()).add("token", token).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(super.hashCode(), token);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof AccountModelWithAuthToken) {
			if (!super.equals(object))
				return false;
			AccountModelWithAuthToken that = (AccountModelWithAuthToken) object;
			return Objects.equal(this.token, that.token);
		}
		return false;
	}
	
	
}
