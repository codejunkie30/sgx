package com.wmsi.sgx.model.sandp.capiq;

import com.google.common.base.Objects;

public class CapIQProperty{

	private String periodType;
	private String startDate;
	private String endDate;
	private String rank;
	private String frequency;
	
	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("periodType", periodType)
			.add("startDate", startDate)
			.add("endDate", endDate)
			.add("rank", rank)
			.add("frequency", frequency)
			.toString();
	}
	
}
