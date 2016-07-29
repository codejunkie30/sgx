package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class CompanyNameAndTicker {
	
	private String companyName;
	private String tickerCode;
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("companyName", companyName).add("tickerCode", tickerCode).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(companyName, tickerCode);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof CompanyNameAndTicker) {
			CompanyNameAndTicker that = (CompanyNameAndTicker) object;
			return Objects.equal(this.companyName, that.companyName) && Objects.equal(this.tickerCode, that.tickerCode);
		}
		return false;
	}
	
	
	

}
