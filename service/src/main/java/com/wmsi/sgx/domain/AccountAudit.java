package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import com.google.common.base.Objects;

@Entity(name = "AccountAudit")
@Table(name = "accounts_audit")
public class AccountAudit extends AbstractAuditable{

	@Id
	@GeneratedValue(generator = "accountAuditGenerator")
	@GenericGenerator(name = "accountAuditGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long audit_id;
	
	@Column(name = "id",nullable = false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private Account.AccountType type;

	@Column(name = "active")
	private Boolean active;
	
	@Column(name = "always_Active")
	private Boolean alwaysActive;

	@Column(name = "start_dt")
	private Date startDate;

	@Column(name = "expiration_dt")
	private Date expirationDate;
	
	@Column(name = "contact_opt_in")
	private Boolean contactOptIn;
	
	@Column(name ="currency")
	private String currency;
	
	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Account.AccountType getType() {
		return type;
	}

	public Boolean getActive() {
		return active;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setType(Account.AccountType type) {
		this.type = type;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Boolean getAlwaysActive() {
		return alwaysActive;
	}

	public void setAlwaysActive(Boolean alwaysActive) {
		this.alwaysActive = alwaysActive;
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

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("id", id)
			.add("user", user)
			.add("type", type)
			.add("active", active)
			.add("startDate", startDate)
			.add("expirationDate", expirationDate)
			.add("alwaysActive", "alwaysActive")
			.add("contactOptIn", "contactOptIn")
			.add("currency", "currency")
			.toString();
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(super.hashCode(), user, type, active, startDate, expirationDate, alwaysActive, contactOptIn, currency );
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AccountAudit) {
	
			AccountAudit that = (AccountAudit) object;
			return Objects.equal(this.user, that.user)
				&& Objects.equal(this.type, that.type)
				&& Objects.equal(this.active, that.active)
				&& Objects.equal(this.startDate, that.startDate)
				&& Objects.equal(this.expirationDate, that.expirationDate)
				&& Objects.equal(this.alwaysActive, that.alwaysActive)
				&& Objects.equal(this.contactOptIn, that.contactOptIn)
				&& Objects.equal(this.currency, that.currency);
		}
		return false;
	}

}
