package com.wmsi.sgx.model.account;

import com.google.common.base.Objects;

public class TrialResponse {
	public String trialDays;
	public String halfwayDays;
	public String getTrialDays() {
		return trialDays;
	}
	public void setTrialDays(String trialDays) {
		this.trialDays = trialDays;
	}
	public String getHalfwayDays() {
		return halfwayDays;
	}
	public void setHalfwayDays(String halfwayDays) {
		this.halfwayDays = halfwayDays;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(trialDays, halfwayDays);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof TrialResponse) {
			TrialResponse that = (TrialResponse) object;
			return Objects.equal(this.trialDays, that.trialDays)
				&& Objects.equal(this.halfwayDays, that.halfwayDays);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("trialDays", trialDays)
			.add("halfwayDays", halfwayDays)
			.toString();
	}
	
}
