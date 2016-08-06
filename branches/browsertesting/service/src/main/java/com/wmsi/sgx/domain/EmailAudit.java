package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "EmailAudit")
@Table(name="email_audit")
public class EmailAudit {
	
	@Id
	@GeneratedValue(generator = "emailAuditGenerator")
	@GenericGenerator(name = "emailAuditGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "email", nullable = false)
	private String email;
	
	@Column(name = "created_dt", nullable = false)
	private Date createdDate = new Date();
	
	@Column(name="body")
	private String body;
	
	@Column(name="watchlist_name")
	private String watchlistName; 
	
	@Column(name="subject", nullable = false)
	private String subject;
	
	@Column(name="status", nullable = false)
	private String status;
	
	@Column(name="reason")
	private String reason;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getWatchlistName() {
		return watchlistName;
	}

	public void setWatchlistName(String watchlistName) {
		this.watchlistName = watchlistName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("email", email).add("watchlistName", watchlistName).add("status", status)
				.toString();
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(email, watchlistName, status);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof EmailAudit) {
			EmailAudit that = (EmailAudit) object;
			return Objects.equal(this.email, that.email) && Objects.equal(this.watchlistName, that.watchlistName)
					&& Objects.equal(this.status, that.status);
		}
		return false;
	}
		
}
