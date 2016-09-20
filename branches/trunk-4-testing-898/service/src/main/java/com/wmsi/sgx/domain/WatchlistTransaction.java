/**
 * 
 */
package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Watchlist transaction entity
 * 
 * @author dt84327
 */
@Entity(name = "WatchlistTransaction")
@Table(name = "watchlist_transaction")
public class WatchlistTransaction extends AbstractAuditable{
	@Id
	@GeneratedValue(generator = "watchlistTransactionsGenerator")
	@GenericGenerator(name = "watchlistTransactionsGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
	private Long id;

	@Column(name = "watchlist_id", nullable = false)
	public Long watchlistId;

	@Column(name = "tickerCode", nullable = false)
	public String tickerCode;

	@Column(name = "[transaction_type]", nullable = false)
	public String transactionType;

	@Column(name = "tradeDate", nullable = false)
	public Date tradeDate;

	@Column(name = "number_of_shares", nullable = false)
	public Double numberOfShares;

	@Column(name = "cost_at_purchase", nullable = false)
	public Double costAtPurchase;

	@Column(name = "current_price", nullable = false)
	public Double currentPrice;

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
	 * @return the watchlistId
	 */
	public Long getWatchlistId() {
		return watchlistId;
	}

	/**
	 * @param watchlistId
	 *            the watchlistId to set
	 */
	public void setWatchlistId(Long watchlistId) {
		this.watchlistId = watchlistId;
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
	public Date getTradeDate() {
		return tradeDate;
	}

	/**
	 * @param tradeDate
	 *            the tradeDate to set
	 */
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
		builder.append("WatchlistTransaction [id=");
		builder.append(id);
		builder.append(", watchlistId=");
		builder.append(watchlistId);
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
