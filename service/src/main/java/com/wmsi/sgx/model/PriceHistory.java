package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

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

	@Override
	public int hashCode(){
		return Objects.hashCode(price, volume);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof PriceHistory) {
			PriceHistory that = (PriceHistory) object;
			return Objects.equal(this.price, that.price)
				&& Objects.equal(this.volume, that.volume);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("price", price)
			.add("volume", volume)
			.toString();
	}
}
