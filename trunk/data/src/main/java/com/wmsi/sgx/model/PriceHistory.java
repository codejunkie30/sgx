package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

public class PriceHistory{

	
	private List<HistoricalValue> price;
	private List<HistoricalValue> highPrice;
	private List<HistoricalValue> lowPrice;
	private List<HistoricalValue> openPrice;
	private List<HistoricalValue> volume;
	
	public List<HistoricalValue> getPrice() {
		return price;
	}

	public void setPrice(List<HistoricalValue> price) {
		this.price = price;
	}
	public List<HistoricalValue> getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(List<HistoricalValue> highPrice) {
		this.highPrice = highPrice;
	}

	public List<HistoricalValue> getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(List<HistoricalValue> lowPrice) {
		this.lowPrice = lowPrice;
	}

	public List<HistoricalValue> getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(List<HistoricalValue> openPrice) {
		this.openPrice = openPrice;
	}

	public List<HistoricalValue> getVolume() {
		return volume;
	}

	public void setVolume(List<HistoricalValue> volume) {
		this.volume = volume;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(price, highPrice, lowPrice, openPrice, volume);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("price", price)
			.add("highPrice", highPrice)
			.add("lowPrice", lowPrice)
			.add("openPrice", openPrice)
			.add("volume", volume)
			.toString();
	}

	@Override
	public boolean equals(Object object){
		if (object instanceof PriceHistory) {
			PriceHistory that = (PriceHistory) object;
			return Objects.equal(this.price, that.price)
				&& Objects.equal(this.highPrice, that.highPrice)
				&& Objects.equal(this.lowPrice, that.lowPrice)
				&& Objects.equal(this.openPrice, that.openPrice)
				&& Objects.equal(this.volume, that.volume);
		}
		return false;
	}	
}
