package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

public class DividendHistory{

	private String tickerCode;
	private List<DividendValue> dividendValues;

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public List<DividendValue> getDividendValues() {
		return dividendValues;
	}

	public void setDividendValues(List<DividendValue> dividendValues) {
		this.dividendValues = dividendValues;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(tickerCode, dividendValues);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof DividendHistory){
			DividendHistory that = (DividendHistory) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
					&& Objects.equal(this.dividendValues, that.dividendValues);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("tickerCode", tickerCode).add("dividendValues", dividendValues)
				.toString();
	}

}