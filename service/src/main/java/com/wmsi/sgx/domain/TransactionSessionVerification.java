/**
 * 
 */
package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Transaction Session Verification entity
 * 
 * @author dt78213
 */
@Entity(name = "TransactionSessionVerification")
@Table(name = "transactionSession_verification")
public class TransactionSessionVerification {
	@Id
	@GeneratedValue(generator = "transactionSessionVerificationGenerator")
	@GenericGenerator(name = "transactionSessionVerificationGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long user_id;

	@Column(name = "token", nullable = false)
	private String token;

	@Column(name = "creationTime", nullable = false)
	private Date creationTime;

	@Column(name = "expiryTime", nullable = false)
	private Date expiryTime;

	@Column(name = "tx_session_token_status", nullable = false)
	private Boolean txSessionTokenStatus;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

	/**
	 * @return the txSessionTokenStatus
	 */
	public boolean getTxSessionTokenStatus() {
		return txSessionTokenStatus;
	}

	/**
	 * @param txSessionTokenStatus
	 *            the txSessionTokenStatus to set
	 */
	public void setTxSessionTokenStatus(Boolean txSessionTokenStatus) {
		this.txSessionTokenStatus = txSessionTokenStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionSessionVerification [id=" + id + ", user_id=" + user_id + ", token=" + token
				+ ", creationTime=" + creationTime + ", expiryTime=" + expiryTime + ", txSessionTokenStatus="
				+ txSessionTokenStatus + "]";
	}

}
