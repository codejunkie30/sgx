package com.wmsi.sgx.model.account;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.domain.Account.AccountType;

public class AccountModel{

	private String email;
	
	private Date startDate;
	
	private Date expirationDate;
	
	private AccountType type;
	
	private Date lastLoginDate;
	
	private Date lastPaymentDate;
	
	private Boolean contactOptIn;
	
	private String currency;
	
	private String token;
	
	private String reason;
	
	private String enetsTranId;
	
	private long daysRemaining;
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

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
	
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public Boolean getContactOptIn() {
		return contactOptIn;
	}

	public void setContactOptIn(Boolean contactOptIn) {
		this.contactOptIn = contactOptIn;
	}
	
	


	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getEnetsTranId() {
		return enetsTranId;
	}

	public void setEnetsTranId(String enetsTranId) {
		this.enetsTranId = enetsTranId;
	}

	/**
	 * @return the daysRemaining
	 */
	public long getDaysRemaining() {
		return daysRemaining;
	}

	/**
	 * @param daysRemaining the daysRemaining to set
	 */
	public void setDaysRemaining(long daysRemaining) {
		this.daysRemaining = daysRemaining;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("email", email).add("startDate", startDate)
				.add("expirationDate", expirationDate).add("type", type).add("lastLoginDate", lastLoginDate)
				.add("lastPaymentDate", lastPaymentDate).add("contactOptIn", contactOptIn).add("currency", currency)
				.add("token", token).add("reason", reason).add("enetsTranId", enetsTranId).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(email, startDate, expirationDate, type, lastLoginDate, lastPaymentDate, contactOptIn,
				currency, token, reason, enetsTranId);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof AccountModel) {
			AccountModel that = (AccountModel) object;
			return Objects.equal(this.email, that.email) && Objects.equal(this.startDate, that.startDate)
					&& Objects.equal(this.expirationDate, that.expirationDate) && Objects.equal(this.type, that.type)
					&& Objects.equal(this.lastLoginDate, that.lastLoginDate)
					&& Objects.equal(this.lastPaymentDate, that.lastPaymentDate)
					&& Objects.equal(this.contactOptIn, that.contactOptIn)
					&& Objects.equal(this.currency, that.currency) && Objects.equal(this.token, that.token)
					&& Objects.equal(this.reason, that.reason) && Objects.equal(this.enetsTranId, that.enetsTranId);
		}
		return false;
	}
		
}
