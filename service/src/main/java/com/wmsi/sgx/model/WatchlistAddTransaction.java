/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

/**
 * Watchlist Add Transaction
 * 
 * @author dt84327
 *
 */
public class WatchlistAddTransaction {

	public String id;

	public List<WatchlistTransactionModel> transactions;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
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

	@Override
	public boolean equals(Object object) {
		if (object instanceof WatchlistAddCompany) {
			WatchlistAddCompany that = (WatchlistAddCompany) object;
			return Objects.equal(this.id, that.id) && Objects.equal(this.transactions, this.transactions);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WatchlistAddTransaction [id=");
		builder.append(id);
		builder.append(", transactions=");
		builder.append(transactions);
		builder.append("]");
		return builder.toString();
	}

}
