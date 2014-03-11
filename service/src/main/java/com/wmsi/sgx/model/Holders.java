package com.wmsi.sgx.model;

import java.util.List;

public class Holders{

	private List<Holder> holders;
	private String tickerCode;
	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public List<Holder> getHolders() {
		return holders;
	}

	public void setHolders(List<Holder> holders) {
		this.holders = holders;
	}
}
