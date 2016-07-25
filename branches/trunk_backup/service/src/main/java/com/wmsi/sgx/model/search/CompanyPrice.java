package com.wmsi.sgx.model.search;

import com.google.common.base.Objects;

public class CompanyPrice {
	public String ticker;
	public String companyName;
	public String currency;
	public Double price;
	public Double change;
	public String getTicker() {
		return ticker;
	}
	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getChange() {
		return change;
	}
	public void setChange(Double change) {
		this.change = change;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(ticker, companyName, currency, price, change);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CompanyPrice) {
			CompanyPrice that = (CompanyPrice) object;
			return Objects.equal(this.ticker, that.ticker)
				&& Objects.equal(this.companyName, that.companyName)
				&& Objects.equal(this.currency, that.currency)
				&& Objects.equal(this.price, that.price)
				&& Objects.equal(this.change, that.change);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("ticker", ticker)
			.add("companyName", companyName)
			.add("currency", currency)
			.add("price", price)
			.add("change", change)
			.toString();
	}
	
	

}
