package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "PremiumVerification")
@Table(name="premium_verification")
public class PremiumVerification{

	@Id
	@GeneratedValue(generator = "premiumVerificationGenerator")
	@GenericGenerator(name = "premiumVerificationGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "token", unique = true, nullable = false)
	private String token;

	@Column(name = "redeemed", nullable = false)
	private Boolean redeemed = false;

	@Column(name = "date", nullable = false)
	private Date date = new Date();
	
	@Column(name = "updated_dt", nullable = false)
	private Date updatedDate = new Date();

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getToken() {
		return token;
	}

	public Boolean getRedeemed() {
		return redeemed;
	}

	public Date getDate() {
		return date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setRedeemed(Boolean redeemed) {
		this.redeemed = redeemed;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the updatedDate
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}

	/**
	 * @param updatedDate the updatedDate to set
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("user", user)
			.add("token", token)
			.add("redeemed", redeemed)
			.add("date", date)
			.add("updatedDate", updatedDate)
			.toString();
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id, user, token, redeemed, date, updatedDate);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof PremiumVerification) {
			PremiumVerification that = (PremiumVerification) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.user, that.user)
				&& Objects.equal(this.token, that.token)
				&& Objects.equal(this.redeemed, that.redeemed)
				&& Objects.equal(this.date, that.date)
			    && Objects.equal(this.updatedDate, that.updatedDate);
		}
		return false;
	}

}
