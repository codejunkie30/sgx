package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class DividendPrice{
	
	private String tickerCode;
	private Double price;
	private Date asOfDate;	
	
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Date getAsOfDate() {
		return asOfDate;
	}
	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(tickerCode, price, asOfDate);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DividendPrice) {
			DividendPrice that = (DividendPrice) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.price, that.price)
				&& Objects.equal(this.asOfDate, that.asOfDate);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("price", price)
			.add("asOfDate", asOfDate)
			.toString();
	}
	
	
}