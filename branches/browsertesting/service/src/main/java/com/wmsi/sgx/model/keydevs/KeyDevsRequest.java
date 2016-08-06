package com.wmsi.sgx.model.keydevs;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class KeyDevsRequest{

	@Valid
	@NotNull(message = "Ticker can not be null")
	private String tickerCode;

	private Object to;
	private Object from;

	public Object getTo() {
		return to;
	}

	public void setTo(Object to) {
		this.to = to;
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
		this.from = from;
	}

	public String getTickerCode() {
		return tickerCode;
	}
	
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
}
