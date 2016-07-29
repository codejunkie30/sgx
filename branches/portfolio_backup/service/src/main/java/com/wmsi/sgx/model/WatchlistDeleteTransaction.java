/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

/**
 * Watchlist Delete Transaction
 * 
 * @author dt84327
 */
public class WatchlistDeleteTransaction {

	public String id;

	public String transactionId;

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
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WatchlistDeleteTransaction [id=");
		builder.append(id);
		builder.append(", transactionId=");
		builder.append(transactionId);
		builder.append("]");
		return builder.toString();
	}
}
