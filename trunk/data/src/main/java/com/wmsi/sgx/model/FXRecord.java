package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class FXRecord {

	private Date date;
	
	private String from;
	
	private String to;
	
	private Double multiplier;
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}

	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(date, from, to, multiplier);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof FXRecord) {
			FXRecord that = (FXRecord) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.from, that.from)
				&& Objects.equal(this.to, that.to)
				&& Objects.equal(this.multiplier, that.multiplier);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("from", from)
			.add("to", to)
			.add("multiplier", multiplier)
			.toString();
	}

	
}
