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

@Entity(name = "PasswordReset")
@Table(name="password_reset")
public class PasswordReset{

	@Override
	public int hashCode(){
		return Objects.hashCode(user, token, isRedeemed, date);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof PasswordReset) {
			PasswordReset that = (PasswordReset) object;
			return Objects.equal(this.user, that.user)
				&& Objects.equal(this.token, that.token)
				&& Objects.equal(this.isRedeemed, that.isRedeemed)
				&& Objects.equal(this.date, that.date);
		}
		return false;
	}

	@Id
	@GeneratedValue(generator = "passwordResetGenerator")
	@GenericGenerator(name = "passwordResetGenerator", strategy = "increment")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "token", unique = true)
	private String token;

	@Column(name = "redeemed", nullable = false )
	private Boolean isRedeemed = false;

	@Column(name = "date", nullable = false)
	private Date date = new Date();

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
		return isRedeemed;
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
		this.isRedeemed = redeemed;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("user", user)
			.add("token", token)
			.add("redeemed", isRedeemed)
			.add("date", date)
			.toString();
	}
	
}
