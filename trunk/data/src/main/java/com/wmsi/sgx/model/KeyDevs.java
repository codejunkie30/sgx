package com.wmsi.sgx.model;

import java.util.List;

public class KeyDevs{

	private String tickerCode;
	private List<KeyDev> keyDevs;
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	public List<KeyDev> getKeyDevs() {
		return keyDevs;
	}
	public void setKeyDevs(List<KeyDev> keyDevs) {
		this.keyDevs = keyDevs;
	}
}
