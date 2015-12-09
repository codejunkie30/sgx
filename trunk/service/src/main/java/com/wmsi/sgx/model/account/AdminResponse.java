package com.wmsi.sgx.model.account;

import java.util.Date;
import com.google.common.base.Objects;

public class AdminResponse {
	public int trialDuration;
	public Date trialDay;
	public Date dateParam;
	public String id;
	public Object data;
	public int responseCode;
	
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
	@Override
	public int hashCode(){
		return Objects.hashCode(trialDuration, trialDay, dateParam, id, data, responseCode);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AdminResponse) {
			AdminResponse that = (AdminResponse) object;
			return Objects.equal(this.trialDuration, that.trialDuration)
				&& Objects.equal(this.trialDay, that.trialDay)
				&& Objects.equal(this.dateParam, that.dateParam)
				&& Objects.equal(this.id, that.id)
				&& Objects.equal(this.data, that.data)
				&& Objects.equal(this.responseCode, that.responseCode);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("trialDuration", trialDuration)
			.add("trialDay", trialDay)
			.add("dateParam", dateParam)
			.add("id", id)
			.add("data", data)
			.add("responseCode", responseCode)
			.toString();
	}
	
	
}
