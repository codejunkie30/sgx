package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.util.MathUtil;

public class Price{

	private static final int DECIMAL_PLACES = 4;

	private Double bidPrice;
	private Double askPrice;
	private Double highPrice;
	private Double lowPrice;
	private Double lastPrice;
	private Double lastTradeVolume;
	private Double openPrice;
	private Double closePrice;
	private Date previousDate;
	private Date currentDate;
	private Date lastTradeTimestamp;
	private String tradingCurrency;
	private Double volume;

	public Double getBidPrice() {
		return bidPrice;
	}

	public Double getAskPrice() {
		return askPrice;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public void setBidPrice(Double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public void setAskPrice(Double askPrice) {
		this.askPrice = askPrice;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

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

	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getLastTradeVolume() {
		return lastTradeVolume;
	}

	public void setLastTradeVolume(Double lastTradeVolume) {
		this.lastTradeVolume = lastTradeVolume;
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
	public int hashCode() {
		return Objects.hashCode(bidPrice, askPrice, highPrice, lowPrice, lastPrice, lastTradeVolume, openPrice,
				closePrice, previousDate, currentDate, lastTradeTimestamp, tradingCurrency, volume);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof Price){
			Price that = (Price) object;
			return Objects.equal(this.bidPrice, that.bidPrice) && Objects.equal(this.askPrice, that.askPrice)
					&& Objects.equal(this.highPrice, that.highPrice) && Objects.equal(this.lowPrice, that.lowPrice)
					&& Objects.equal(this.lastPrice, that.lastPrice)
					&& Objects.equal(this.lastTradeVolume, that.lastTradeVolume)
					&& Objects.equal(this.openPrice, that.openPrice) && Objects.equal(this.closePrice, that.closePrice)
					&& Objects.equal(this.previousDate, that.previousDate)
					&& Objects.equal(this.currentDate, that.currentDate)
					&& Objects.equal(this.lastTradeTimestamp, that.lastTradeTimestamp)
					&& Objects.equal(this.tradingCurrency, that.tradingCurrency)
					&& Objects.equal(this.volume, that.volume);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("bidPrice", bidPrice).add("askPrice", askPrice)
				.add("highPrice", highPrice).add("lowPrice", lowPrice).add("lastPrice", lastPrice)
				.add("lastTradeVolume", lastTradeVolume).add("openPrice", openPrice).add("closePrice", closePrice)
				.add("previousDate", previousDate).add("currentDate", currentDate)
				.add("lastTradeTimestamp", lastTradeTimestamp).add("tradingCurrency", tradingCurrency)
				.add("volume", volume).toString();
	}

}
