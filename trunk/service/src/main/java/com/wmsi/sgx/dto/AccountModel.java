package com.wmsi.sgx.dto;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.domain.Account.AccountType;

public class AccountModel{

	private String email;
	
	private Date startDate;
	
	private Date expirationDate;
	
	private AccountType type;

	public String getEmail() {
		return email;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public AccountType getType() {
		return type;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("email", email).add("startDate", startDate)
				.add("expirationDate", expirationDate).add("type", type).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(email, startDate, expirationDate, type);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof AccountModel){
			AccountModel that = (AccountModel) object;
			return Objects.equal(this.email, that.email) && Objects.equal(this.startDate, that.startDate)
					&& Objects.equal(this.expirationDate, that.expirationDate) && Objects.equal(this.type, that.type);
		}
		return false;
	}
		
}
