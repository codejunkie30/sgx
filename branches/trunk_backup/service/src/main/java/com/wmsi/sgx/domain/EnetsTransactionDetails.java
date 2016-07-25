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

@Entity(name = "enets")
@Table(name="enets")
public class EnetsTransactionDetails {
	
	@Id
	@GeneratedValue(generator = "enetsIdGenerator")
	@GenericGenerator(name = "enetsIdGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "trans_dt", nullable = false)
	private Date trans_dt = new Date();
	
	@Column(name = "active", nullable = false)
	private Boolean active;
	
	@Column(name = "trans_id", nullable = false)
	private String trans_id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getTrans_dt() {
		return trans_dt;
	}

	public void setTrans_dt(Date trans_dt) {
		this.trans_dt = trans_dt;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("user", user).add("trans_dt", trans_dt).add("active", active)
				.add("trans_id", trans_id).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(user, trans_dt, active, trans_id);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof EnetsTransactionDetails) {
			EnetsTransactionDetails that = (EnetsTransactionDetails) object;
			return Objects.equal(this.user, that.user) && Objects.equal(this.trans_dt, that.trans_dt)
					&& Objects.equal(this.active, that.active) && Objects.equal(this.trans_id, that.trans_id);
		}
		return false;
	}
	
	
	
	
}
