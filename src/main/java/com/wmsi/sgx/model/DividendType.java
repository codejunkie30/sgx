package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class DividendType{
	
	private String tickerCode;
	private String divType;
	private Date asOfDate;
	
	public String getDivType() {
		return divType;
	}
	public void setDivType(String type) {
		this.divType = type;
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
		return Objects.hashCode(tickerCode, divType, asOfDate);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DividendType) {
			DividendType that = (DividendType) object;
			return Objects.equal(this.tickerCode, that.tickerCode)
				&& Objects.equal(this.divType, that.divType)
				&& Objects.equal(this.asOfDate, that.asOfDate);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("tickerCode", tickerCode)
			.add("divType", divType)
			.add("asOfDate", asOfDate)
			.toString();
	}
	
}