/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author dt84327
 *
 */
public class WatchlistTransactionModel {

	private Long id;

	private String tickerCode;

	private String transactionType;

	private Date tradeDate;

	private Double numberOfShares;

	private Double costAtPurchase;

	private Double currentPrice;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the tickerCode
	 */
	public String getTickerCode() {
		return tickerCode;
	}

	/**
	 * @param tickerCode
	 *            the tickerCode to set
	 */
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	/**
	 * @return the transactionType
	 */
	public String getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType
	 *            the transactionType to set
	 */
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the tradeDate
	 */
	@JsonSerialize(using=JsonDateInMillisecondsSerializer.class)
	public Date getTradeDate() {
		return tradeDate;
	}

	/**
	 * @param tradeDate
	 *            the tradeDate to set
	 */
	@JsonSerialize(using=JsonDateSerializer.class)
	public void setTradeDate(Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	/**
	 * @return the numberOfShares
	 */
	public Double getNumberOfShares() {
		return numberOfShares;
	}

	/**
	 * @param numberOfShares
	 *            the numberOfShares to set
	 */
	public void setNumberOfShares(Double numberOfShares) {
		this.numberOfShares = numberOfShares;
	}

	/**
	 * @return the costAtPurchase
	 */
	public Double getCostAtPurchase() {
		return costAtPurchase;
	}

	/**
	 * @param costAtPurchase
	 *            the costAtPurchase to set
	 */
	public void setCostAtPurchase(Double costAtPurchase) {
		this.costAtPurchase = costAtPurchase;
	}

	/**
	 * @return the currentPrice
	 */
	public Double getCurrentPrice() {
		return currentPrice;
	}

	/**
	 * @param currentPrice
	 *            the currentPrice to set
	 */
	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WatchlistTransactionModel [id=");
		builder.append(id);
		builder.append(", tickerCode=");
		builder.append(tickerCode);
		builder.append(", transactionType=");
		builder.append(transactionType);
		builder.append(", tradeDate=");
		builder.append(tradeDate);
		builder.append(", numberOfShares=");
		builder.append(numberOfShares);
		builder.append(", costAtPurchase=");
		builder.append(costAtPurchase);
		builder.append(", currentPrice=");
		builder.append(currentPrice);
		builder.append("]");
		return builder.toString();
	}

}
