package com.wmsi.sgx.service.quanthouse.feedos;

import java.util.Date;

import com.google.common.base.Objects;

public class FeedOSData{

	private Double ask;
	private Double bid;
	private Double closePrice;
	private Date currentBusinessDay;
	private Double highPrice;
	private Integer id;
	private String isin;
	private Date lastOffBookTradeTimestamp;
	private Double lastPrice;
	private Double lastTradePrice;
	private Date lastTradeTimestamp;
	private double lastTradeVolume;
	private Double lowPrice;
	private String market;
	private Double openPrice;
	private Date previousBusinessDay;
	private String tradingCurrency;
	private String tradingSymbol;
	private Double totalVolume;

	public Double getAsk() {
		return ask;
	}

	public Double getBid() {
		return bid;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public Date getCurrentBusinessDay() {
		return currentBusinessDay;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public Integer getId() {
		return id;
	}

	public String getIsin() {
		return isin;
	}

	public Date getLastOffBookTradeTimestamp() {
		return lastOffBookTradeTimestamp;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	public Double getLastTradePrice() {
		return lastTradePrice;
	}

	public Date getLastTradeTimestamp() {
		return lastTradeTimestamp;
	}

	public double getLastTradeVolume() {
		return lastTradeVolume;
	}

	public Double getLowPrice() {
		return lowPrice;
	}

	public String getMarket() {
		return market;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public Date getPreviousBusinessDay() {
		return previousBusinessDay;
	}

	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public String getTradingSymbol() {
		return tradingSymbol;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}

	public void setAsk(Double ask) {
		this.ask = ask;
	}

	public void setBid(Double bid) {
		this.bid = bid;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public void setCurrentBusinessDay(Date currentBusinessDay) {
		this.currentBusinessDay = currentBusinessDay;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public void setLastOffBookTradeTimestamp(Date lastOffBookTradeTimestamp) {
		this.lastOffBookTradeTimestamp = lastOffBookTradeTimestamp;
	}

	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setLastTradePrice(Double lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
	}

	public void setLastTradeTimestamp(Date lastTradeTimestamp) {
		this.lastTradeTimestamp = lastTradeTimestamp;
	}

	public void setLastTradeVolume(double lastTradeVolume) {
		this.lastTradeVolume = lastTradeVolume;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public void setPreviousBusinessDay(Date previousBusinessDay) {
		this.previousBusinessDay = previousBusinessDay;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}

	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("ask", ask).add("bid", bid).add("closePrice", closePrice)
				.add("currentBusinessDay", currentBusinessDay).add("highPrice", highPrice).add("id", id)
				.add("isin", isin).add("lastOffBookTradeTimestamp", lastOffBookTradeTimestamp)
				.add("lastPrice", lastPrice).add("lastTradePrice", lastTradePrice)
				.add("lastTradeTimestamp", lastTradeTimestamp).add("lastTradeVolume", lastTradeVolume)
				.add("lowPrice", lowPrice).add("market", market).add("openPrice", openPrice)
				.add("previousBusinessDay", previousBusinessDay).add("tradingCurrency", tradingCurrency)
				.add("tradingSymbol", tradingSymbol).add("totalVolume", totalVolume).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ask, bid, closePrice, currentBusinessDay, highPrice, id, isin,
				lastOffBookTradeTimestamp, lastPrice, lastTradePrice, lastTradeTimestamp, lastTradeVolume, lowPrice,
				market, openPrice, previousBusinessDay, tradingCurrency, tradingSymbol, totalVolume);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof FeedOSData){
			FeedOSData that = (FeedOSData) object;
			return Objects.equal(this.ask, that.ask) && Objects.equal(this.bid, that.bid)
					&& Objects.equal(this.closePrice, that.closePrice)
					&& Objects.equal(this.currentBusinessDay, that.currentBusinessDay)
					&& Objects.equal(this.highPrice, that.highPrice) && Objects.equal(this.id, that.id)
					&& Objects.equal(this.isin, that.isin)
					&& Objects.equal(this.lastOffBookTradeTimestamp, that.lastOffBookTradeTimestamp)
					&& Objects.equal(this.lastPrice, that.lastPrice)
					&& Objects.equal(this.lastTradePrice, that.lastTradePrice)
					&& Objects.equal(this.lastTradeTimestamp, that.lastTradeTimestamp)
					&& Objects.equal(this.lastTradeVolume, that.lastTradeVolume)
					&& Objects.equal(this.lowPrice, that.lowPrice) && Objects.equal(this.market, that.market)
					&& Objects.equal(this.openPrice, that.openPrice)
					&& Objects.equal(this.previousBusinessDay, that.previousBusinessDay)
					&& Objects.equal(this.tradingCurrency, that.tradingCurrency)
					&& Objects.equal(this.tradingSymbol, that.tradingSymbol)
					&& Objects.equal(this.totalVolume, that.totalVolume);
		}
		return false;
	}

}