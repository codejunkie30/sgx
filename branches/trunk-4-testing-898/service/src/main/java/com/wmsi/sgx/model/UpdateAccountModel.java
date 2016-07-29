package com.wmsi.sgx.model;

public class UpdateAccountModel {
	private String currency;
	private Boolean contactOptIn;
	private String email;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Boolean getContactOptIn() {
		return contactOptIn;
	}
	public void setContactOptIn(Boolean contactOptIn) {
		this.contactOptIn = contactOptIn;
	}
	@Override
	public String toString() {
		return "UpdateAccountModel [currency=" + currency + ", contactOptIn=" + contactOptIn + ", email=" + email + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactOptIn == null) ? 0 : contactOptIn.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		UpdateAccountModel other = (UpdateAccountModel) obj;
		if (contactOptIn == null) {
			if (other.contactOptIn != null)
				return false;
		} else if (!contactOptIn.equals(other.contactOptIn))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
		
	
}
