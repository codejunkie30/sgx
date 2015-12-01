package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "UserLogin")
@Table(name = "user_login")
@IdClass(UserComposite.class)
public class UserLogin{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator = "userLoginGenerator")
	@GenericGenerator(name = "userLoginGenerator", strategy = "increment")
	private Long id;
	
	@Id
	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "success", nullable = false)
	private Boolean success;

	@Column(name = "ipaddress", nullable = false)
	private String ipAddress;
	
	@Id
	@Column(name = "date", nullable = false)
	private Date date = new Date();

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public Boolean getSuccess() {
		return success;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Date getDate() {
		return date;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(username, success, ipAddress, date);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof UserLogin) {
			UserLogin that = (UserLogin) object;
			return Objects.equal(this.username, that.username)
				&& Objects.equal(this.success, that.success)
				&& Objects.equal(this.ipAddress, that.ipAddress)
				&& Objects.equal(this.date, that.date);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("username", username)
			.add("success", success)
			.add("ipAddress", ipAddress)
			.add("date", date)
			.toString();
	}
	
}
