package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class DividendValue{
	
	private Date dividendExDate;
	private Date dividendPayDate;
	private String dividendType;
	private Double dividendPrice;
	
	public Date getDividendExDate() {
		return dividendExDate;
	}
	public void setDividendExDate(Date dividendExDate) {
		this.dividendExDate = dividendExDate;
	}
	public Date getDividendPayDate() {
		return dividendPayDate;
	}
	public void setDividendPayDate(Date dividendPayDate) {
		this.dividendPayDate = dividendPayDate;
	}
	public String getDividendType() {
		return dividendType;
	}
	public void setDividendType(String dividendType) {
		this.dividendType = dividendType;
	}
	public Double getDividendPrice() {
		return dividendPrice;
	}
	public void setDividendPrice(Double dividendPrice) {
		this.dividendPrice = dividendPrice;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(dividendExDate, dividendPayDate, dividendType, dividendPrice);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DividendValue) {
			DividendValue that = (DividendValue) object;
			return Objects.equal(this.dividendExDate, that.dividendExDate)
				&& Objects.equal(this.dividendPayDate, that.dividendPayDate)
				&& Objects.equal(this.dividendType, that.dividendType)
				&& Objects.equal(this.dividendPrice, that.dividendPrice);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("dividendExDate", dividendExDate)
			.add("dividendPayDate", dividendPayDate)
			.add("dividendType", dividendType)
			.add("dividendPrice", dividendPrice)
			.toString();
	}
	
	
	
}