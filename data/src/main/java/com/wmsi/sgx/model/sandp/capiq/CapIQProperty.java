package com.wmsi.sgx.model.sandp.capiq;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class CapIQProperty{
	
	@JsonProperty("periodtype")
	private String periodType;
	public String getPeriodType(){return periodType;}
	public void setPeriodType(String t){periodType = t;}
	
	@JsonProperty("startdate")
	private String startDate;
	public String getStartDate(){return startDate;}
	public void setStartDate(String d){this.startDate = d;}
	
	@JsonProperty("enddate")
	private String endDate;
	public String getEndDate(){return endDate;}
	public void setEndDate(String d){endDate = d;}
	
	@JsonProperty("rank")
	private String rank;
	public String getRank(){return rank;}
	public void setRank(String r){rank = r;}
	
	@JsonProperty("frequency")
	private String frequency;
	public String getFrequency(){return frequency;}
	public void setFrequency(String f){frequency = f;}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(periodType, startDate, endDate, rank, frequency);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQProperty) {
			CapIQProperty that = (CapIQProperty) object;
			return Objects.equal(this.periodType, that.periodType)
				&& Objects.equal(this.startDate, that.startDate)
				&& Objects.equal(this.endDate, that.endDate)
				&& Objects.equal(this.rank, that.rank)
				&& Objects.equal(this.frequency, that.frequency);
		}
		return false;
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
