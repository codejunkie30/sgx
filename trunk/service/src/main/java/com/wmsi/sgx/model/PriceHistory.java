package com.wmsi.sgx.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("priceHistory")
public class PriceHistory{

	private List<HistoricalValue> price;
	private List<HistoricalValue> volume;
	public List<HistoricalValue> getPrice() {
		return price;
	}
	public void setPrice(List<HistoricalValue> price) {
		this.price = price;
	}
	public List<HistoricalValue> getVolume() {
		return volume;
	}
	public void setVolume(List<HistoricalValue> volume) {
		this.volume = volume;
	}

}
