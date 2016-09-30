package com.wmsi.sgx.model.account;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.wmsi.sgx.model.JsonDateSerializer;

public class AdminResponse {
	public int trialDuration;
	public Date trialDay;
	public Date dateParam;
	public String id;
	public Object data;
	public int responseCode;
	private String TransId;
	private String Username;
	
	public int getTrialDuration() {
		return trialDuration;
	}
	public void setTrialDuration(int trialDuration) {
		this.trialDuration = trialDuration;
	}
	public Date getTrialDay() {
		return trialDay;
	}
	public void setTrialDay(Date trialDay) {
		this.trialDay = trialDay;
	}
	public Date getDateParam() {
		return dateParam;
	}
	@JsonSerialize(using=JsonDateSerializer.class)
	public void setDateParam(Date dateParam) {
		this.dateParam = dateParam;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getTransId() {
		return TransId;
	}
	public void setTransId(String transId) {
		TransId = transId;
	}
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("trialDuration", trialDuration).add("trialDay", trialDay)
				.add("dateParam", dateParam).add("id", id).add("data", data).add("responseCode", responseCode)
				.add("TransId", TransId).add("Username", Username).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(trialDuration, trialDay, dateParam, id, data, responseCode, TransId, Username);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof AdminResponse) {
			AdminResponse that = (AdminResponse) object;
			return Objects.equal(this.trialDuration, that.trialDuration) && Objects.equal(this.trialDay, that.trialDay)
					&& Objects.equal(this.dateParam, that.dateParam) && Objects.equal(this.id, that.id)
					&& Objects.equal(this.data, that.data) && Objects.equal(this.responseCode, that.responseCode)
					&& Objects.equal(this.TransId, that.TransId) && Objects.equal(this.Username, that.Username);
		}
		return false;
	}
	
	
}
