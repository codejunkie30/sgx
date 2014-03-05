package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class HistoricalValue{

	private Date date;
	private String tickerCode;
	private Double price;
	private Double volume;
	
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("tickerCode", tickerCode)
			.add("price", price)
			.add("volume", volume)
			.toString();
	}
}
