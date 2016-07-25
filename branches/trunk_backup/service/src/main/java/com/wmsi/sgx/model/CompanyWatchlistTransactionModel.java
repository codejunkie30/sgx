/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.Date;
import java.util.List;

/**
 * @author dt84327
 *
 */
public class CompanyWatchlistTransactionModel {

	private Date tradeDate;

	private Double numberOfShares = 0.0;

	private Double investement = 0.0;

	private List<WatchlistTransactionModel> transactions;

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
	 * @return the investement
	 */
	public Double getInvestement() {
		return investement;
	}

	/**
	 * @param investement
	 *            the investement to set
	 */
	public void setInvestement(Double investement) {
		this.investement = investement;
	}

	/**
	 * @return the transactions
	 */
	public List<WatchlistTransactionModel> getTransactions() {
		return transactions;
	}

	/**
	 * @param transactions
	 *            the transactions to set
	 */
	public void setTransactions(List<WatchlistTransactionModel> transactions) {
		this.transactions = transactions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyWatchlistTransactionModel [tradeDate=");
		builder.append(tradeDate);
		builder.append(", numberOfShares=");
		builder.append(numberOfShares);
		builder.append(", investement=");
		builder.append(investement);
		builder.append(", transactions=");
		builder.append(transactions);
		builder.append("]");
		return builder.toString();
	}
}
