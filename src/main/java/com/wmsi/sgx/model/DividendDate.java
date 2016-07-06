package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class DividendDate{
	
	private String tickerCode;
	private Date dateValue;
	private Date asOfDate;
	
	
	public Date getDateValue() {
		return dateValue;
	}
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
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
		return Objects.hashCode(tickerCode, dateValue, asOfDate);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DividendDate) {
			DividendDate that = (DividendDate) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.dateValue, that.dateValue)
				&& Objects.equal(this.asOfDate, that.asOfDate);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("dateValue", dateValue)
			.add("asOfDate", asOfDate)
			.toString();
	}
	
	
	
	
}