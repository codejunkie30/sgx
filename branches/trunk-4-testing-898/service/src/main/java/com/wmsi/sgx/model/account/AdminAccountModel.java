package com.wmsi.sgx.model.account;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.wmsi.sgx.model.JsonDateSerializer;

public class AdminAccountModel {
	public String username;
	public Date created_date;
	public Date expiration_date;
	public String status;
	public String transId;
	public Date enetsTransDt;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getCreated_date() {
		return created_date;
	}
	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}
	@JsonSerialize(using=JsonDateSerializer.class)
	public Date getExpiration_date() {
		return expiration_date;
	}
	public void setExpiration_date(Date expiration_date) {
		this.expiration_date = expiration_date;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public Date getEnetsTransDt() {
		return enetsTransDt;
	}
	public void setEnetsTransDt(Date enetsTransDt) {
		this.enetsTransDt = enetsTransDt;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("username", username).add("created_date", created_date)
				.add("expiration_date", expiration_date).add("status", status).add("transId", transId)
				.add("enetsTransDt", enetsTransDt).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(username, created_date, expiration_date, status, transId, enetsTransDt);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof AdminAccountModel) {
			AdminAccountModel that = (AdminAccountModel) object;
			return Objects.equal(this.username, that.username) && Objects.equal(this.created_date, that.created_date)
					&& Objects.equal(this.expiration_date, that.expiration_date)
					&& Objects.equal(this.status, that.status) && Objects.equal(this.transId, that.transId)
					&& Objects.equal(this.enetsTransDt, that.enetsTransDt);
		}
		return false;
	}
	
	
}
