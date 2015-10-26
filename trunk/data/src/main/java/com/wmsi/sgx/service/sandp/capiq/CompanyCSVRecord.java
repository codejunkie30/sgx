package com.wmsi.sgx.service.sandp.capiq;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class CompanyCSVRecord {
	
	private String ticker;
	
	private String exchange;
	
	private String name;
	
	private String value;
	
	private String period;
	
	// assume it's the latest date if not set
	private Date periodDate = new Date();
	
	private String currency;

	public String getTicker() {
		return StringUtils.stripToNull(ticker);
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getExchange() {
		return StringUtils.stripToNull(exchange);
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getName() {
		return StringUtils.stripToNull(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return StringUtils.stripToNull(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPeriod() {
		return StringUtils.stripToNull(period);
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Date getPeriodDate() {
		return periodDate;
	}

	public void setPeriodDate(Date periodDate) {
		this.periodDate = periodDate;
	}
	
	public String getCurrency() {
		return StringUtils.stripToNull(currency);
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "CompanyCSVRecord [ticker=" + ticker + ", exchange=" + exchange
				+ ", name=" + name + ", value=" + value + ", period=" + period
				+ ", periodDate=" + periodDate + ", currency=" + currency + "]";
	}
	
}