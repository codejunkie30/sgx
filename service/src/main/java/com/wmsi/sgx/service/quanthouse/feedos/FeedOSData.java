package com.wmsi.sgx.service.quanthouse.feedos;

import java.util.Date;
import com.google.common.base.Objects;

public class FeedOSData{

	private Double lastPrice;
	private Double openPrice;
	private Double closePrice;
	private Date currentBusinessDay;
	private Date previousBusinessDay;
	private Date lastTradeTimestamp;
	private Date lastOffBookTradeTimestamp;	
	
	public Double getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(Double l) {
		lastPrice = l;
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

	public Date getCurrentBusinessDay() {
		return currentBusinessDay;
	}

	public void setCurrentBusinessDay(Date d) {
		currentBusinessDay = d;
	}

	public Date getPreviousBusinessDay() {
		return previousBusinessDay;
	}

	public void setPreviousBusinessDay(Date d) {
		previousBusinessDay = d;
	}

	public Date getLastTradeTimestamp() {
		return lastTradeTimestamp;
	}

	public void setLastTradeTimestamp(Date l) {
		lastTradeTimestamp = l;
	}

	public Date getLastOffBookTradeTimestamp() {
		return lastOffBookTradeTimestamp;
	}

	public void setLastOffBookTradeTimestamp(Date l) {
		lastOffBookTradeTimestamp = l;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(lastPrice, openPrice, closePrice, previousBusinessDay, lastTradeTimestamp, lastOffBookTradeTimestamp, currentBusinessDay);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof FeedOSData) {
			FeedOSData that = (FeedOSData) object;
			return Objects.equal(this.lastPrice, that.lastPrice)
				&& Objects.equal(this.openPrice, that.openPrice)
				&& Objects.equal(this.closePrice, that.closePrice)
				&& Objects.equal(this.previousBusinessDay, that.previousBusinessDay)
				&& Objects.equal(this.lastTradeTimestamp, that.lastTradeTimestamp)
				&& Objects.equal(this.lastOffBookTradeTimestamp, that.lastOffBookTradeTimestamp)
				&& Objects.equal(this.currentBusinessDay, that.currentBusinessDay);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("lastPrice", lastPrice)
			.add("openPrice", openPrice)
			.add("closePrice", closePrice)
			.add("previousBusinessDay", previousBusinessDay)
			.add("lastTradeTimestamp", lastTradeTimestamp)
			.add("lastOffBookTradeTimestamp", lastOffBookTradeTimestamp)
			.add("currentBusinessDay", currentBusinessDay)
			.toString();
	}
}
