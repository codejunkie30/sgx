package com.wmsi.sgx.model;

import java.util.List;
import java.util.Date;

import com.google.common.base.Objects;

public class DividendHistory{

	private List<DividendDate> dividendExDate;
	private List<DividendDate> dividendPayDate;
	private List<DividendType> dividendType;
	private List<DividendPrice> dividendPrice;
	
	public List<DividendDate> getDividendExDate() {
		return dividendExDate;
	}

	public void setDividendExDate(List<DividendDate> dividendExDate) {
		this.dividendExDate = dividendExDate;
	}

	public List<DividendDate> getDividendPayDate() {
		return dividendPayDate;
	}

	public void setDividendPayDate(List<DividendDate> dividendPayDate) {
		this.dividendPayDate = dividendPayDate;
	}

	public List<DividendType> getDividendType() {
		return dividendType;
	}

	public void setDividendType(List<DividendType> dividendType) {
		this.dividendType = dividendType;
	}

	public List<DividendPrice> getDividendPrice() {
		return dividendPrice;
	}

	public void setDividendPrice(List<DividendPrice> dividendPrice) {
		this.dividendPrice = dividendPrice;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(dividendExDate, dividendPayDate, dividendType, dividendPrice);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DividendHistory) {
			DividendHistory that = (DividendHistory) object;
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