package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.common.base.Objects;

@Entity(name = "TradeEvents")
@Table(name = "trade_events")
public class TradeEvent implements Comparable<TradeEvent>{

	@Id
	@GeneratedValue(generator = "priceGenerator")
	@GenericGenerator(name = "priceGenerator", strategy = "increment")
	private Long id;

	@Column(name = "market")
	private String market;

	@Column(name = "ticker")
	private String ticker;

	@Column(name = "last_trade_time")
	private Date lastTradeTime;

	@Column(name = "price")
	private Double lastPrice;

	@Column(name = "volume")
	private Double volume;

	@Column(name = "last_trade_price")
	private Double lastTradeVolume;

	@Column(name = "last_trade_volume")
	private Double lastTradePrice;

	@Column(name = "bid")
	private Double bidPrice;

	@Column(name = "ask")
	private Double askPrice;

	@Column(name = "high_price")
	private Double highPrice;

	@Column(name = "low_price")
	private Double lowPrice;

	@Column(name = "open_price")
	private Double openPrice;

	@Column(name = "close_price")
	private Double closePrice;

	@Column(name = "previous_close_date")
	private Date previousDate;

	@Column(name = "trade_date")
	private Date currentDate;

	@Column(name = "currency")
	private String tradingCurrency;

	public Long getId() {
		return id;
	}

	public String getMarket() {
		return market;
	}

	public String getTicker() {
		return ticker;
	}

	public Date getLastTradeTime() {
		return lastTradeTime;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	public Double getVolume() {
		return volume;
	}

	public Double getLastTradeVolume() {
		return lastTradeVolume;
	}

	public Double getLastTradePrice() {
		return lastTradePrice;
	}

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

	public Double getOpenPrice() {
		return openPrice;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public Date getPreviousDate() {
		return previousDate;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public String getTradingCurrency() {
		return tradingCurrency;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setLastTradeTime(Date lastTradeTime) {
		this.lastTradeTime = lastTradeTime;
	}

	public void setLastPrice(Double lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public void setLastTradeVolume(Double lastTradeVolume) {
		this.lastTradeVolume = lastTradeVolume;
	}

	public void setLastTradePrice(Double lastTradePrice) {
		this.lastTradePrice = lastTradePrice;
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

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public void setPreviousDate(Date previousDate) {
		this.previousDate = previousDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public void setTradingCurrency(String tradingCurrency) {
		this.tradingCurrency = tradingCurrency;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("market", market).add("ticker", ticker)
				.add("lastTradeTime", lastTradeTime).add("lastPrice", lastPrice).add("volume", volume)
				.add("lastTradeVolume", lastTradeVolume).add("lastTradePrice", lastTradePrice)
				.add("bidPrice", bidPrice).add("askPrice", askPrice).add("highPrice", highPrice)
				.add("lowPrice", lowPrice).add("openPrice", openPrice).add("closePrice", closePrice)
				.add("previousDate", previousDate).add("currentDate", currentDate)
				.add("tradingCurrency", tradingCurrency).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(market, ticker, lastTradeTime, lastPrice, volume, lastTradeVolume, lastTradePrice,
				bidPrice, askPrice, highPrice, lowPrice, openPrice, closePrice, previousDate, currentDate,
				tradingCurrency);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof TradeEvent){
			TradeEvent that = (TradeEvent) object;
			return Objects.equal(this.market, that.market) && Objects.equal(this.ticker, that.ticker)
					&& Objects.equal(this.lastTradeTime, that.lastTradeTime)
					&& Objects.equal(this.lastPrice, that.lastPrice) && Objects.equal(this.volume, that.volume)
					&& Objects.equal(this.lastTradeVolume, that.lastTradeVolume)
					&& Objects.equal(this.lastTradePrice, that.lastTradePrice)
					&& Objects.equal(this.bidPrice, that.bidPrice) && Objects.equal(this.askPrice, that.askPrice)
					&& Objects.equal(this.highPrice, that.highPrice) && Objects.equal(this.lowPrice, that.lowPrice)
					&& Objects.equal(this.openPrice, that.openPrice) && Objects.equal(this.closePrice, that.closePrice)
					&& Objects.equal(this.previousDate, that.previousDate)
					&& Objects.equal(this.currentDate, that.currentDate)
					&& Objects.equal(this.tradingCurrency, that.tradingCurrency);
		}
		return false;
	}

	@Override
	public int compareTo(TradeEvent o) {
		return this.lastTradeTime.compareTo(o.lastTradeTime);
	}

}
