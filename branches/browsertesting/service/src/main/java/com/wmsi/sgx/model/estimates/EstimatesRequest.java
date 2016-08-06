package com.wmsi.sgx.model.estimates;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class EstimatesRequest {

	@Valid
	@NotNull(message = "Ticker can not be null")
	private String tickerCode;

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	
	
}
