package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.util.MathUtil;

public class Price{

	private static final int DECIMAL_PLACES = 4;

	private Double lastPrice;
	private Double openPrice;
	private Double closePrice;
	private Date previousDate;
	private Date currentDate;
	private Date lastTradeTimestamp;
	
	public Double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(Double p) {
		lastPrice = p;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(Double p) {
		openPrice = p;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(Double p) {
		closePrice = p;
	}

	public Date getPreviousDate() {
		return previousDate;
	}

	public void setPreviousDate(Date d) {
		previousDate = d;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date d) {
		currentDate = d;
	}

	public Date getLastTradeTimestamp() {
		return lastTradeTimestamp;
	}

	public void setLastTradeTimestamp(Date t) {
		lastTradeTimestamp = t;
	}

	public Double getChange() {
		Double change = 0.0D;

		if(closePrice != null && lastPrice != null){
			change = MathUtil.change(closePrice, lastPrice, DECIMAL_PLACES);
		}

		return change;
	}

	public Double getPercentChange() {
		Double percentChange = 0.0D;

		if(closePrice != null && lastPrice != null){
			percentChange = MathUtil.percentChange(closePrice, lastPrice, DECIMAL_PLACES);
		}

		return percentChange;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(lastPrice, openPrice, closePrice, previousDate, currentDate, lastTradeTimestamp);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Price) {
			Price that = (Price) object;
			return Objects.equal(this.lastPrice, that.lastPrice)
				&& Objects.equal(this.openPrice, that.openPrice)
				&& Objects.equal(this.closePrice, that.closePrice)
				&& Objects.equal(this.previousDate, that.previousDate)
				&& Objects.equal(this.currentDate, that.currentDate)
				&& Objects.equal(this.lastTradeTimestamp, that.lastTradeTimestamp);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("DECIMAL_PLACES", DECIMAL_PLACES)
			.add("lastPrice", lastPrice)
			.add("openPrice", openPrice)
			.add("closePrice", closePrice)
			.add("previousDate", previousDate)
			.add("currentDate", currentDate)
			.add("lastTradeTimestamp", lastTradeTimestamp)
			.toString();
	}

}
