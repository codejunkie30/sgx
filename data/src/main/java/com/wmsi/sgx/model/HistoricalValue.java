package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class HistoricalValue{


	private String tickerCode;
	private Date date;
	private Double value;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("date", date)
			.add("value", value)
			.toString();
	}
}
