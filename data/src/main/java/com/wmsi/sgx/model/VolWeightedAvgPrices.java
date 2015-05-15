package com.wmsi.sgx.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.base.Objects;

@JsonRootName("vwaps")
public class VolWeightedAvgPrices{
	private String tickerCode;
	
	@JsonUnwrapped
	private List<VolWeightedAvgPrice> vwaps;

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public List<VolWeightedAvgPrice> getVwaps() {
		return vwaps;
	}

	public void setVwaps(List<VolWeightedAvgPrice> vwaps) {
		this.vwaps = vwaps;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(tickerCode, vwaps);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof VolWeightedAvgPrices) {
			VolWeightedAvgPrices that = (VolWeightedAvgPrices) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.vwaps, that.vwaps);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("vwaps", vwaps)
			.toString();
	}
	
}