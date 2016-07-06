package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class VolWeightedAvgPrice{
	private Date date;
	private String currency;
	private String exchange;
	private String tickerCode;
	private String value;
	private String volume;	
	private String adjustmentFactorValue;
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getAdjustmentFactorValue() {
		return adjustmentFactorValue;
	}

	public void setAdjustmentFactorValue(String adjustmentFactorValue) {
		this.adjustmentFactorValue = adjustmentFactorValue;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(date, currency, exchange, tickerCode, value, volume);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof VolWeightedAvgPrice) {
			VolWeightedAvgPrice that = (VolWeightedAvgPrice) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.currency, that.currency)
				&& Objects.equal(this.exchange, that.exchange)
				&& Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.value, that.value)
				&& Objects.equal(this.volume, that.volume);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("currency", currency)
			.add("exchange", exchange)
			.add("tickerCode", tickerCode)
			.add("value", value)
			.add("volume", volume)
			.toString();
	}
	
}