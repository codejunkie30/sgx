/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.List;

/**
 * @author dt84327
 *
 */
public class StockListPriceHistory {

	private List<StockListCompanyPriceHistory> companiesPriceHistory;

	/**
	 * @return the companiesPriceHistory
	 */
	public List<StockListCompanyPriceHistory> getCompaniesPriceHistory() {
		return companiesPriceHistory;
	}

	/**
	 * @param companiesPriceHistory
	 *            the companiesPriceHistory to set
	 */
	public void setCompaniesPriceHistory(List<StockListCompanyPriceHistory> companiesPriceHistory) {
		this.companiesPriceHistory = companiesPriceHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockListPriceHistory [companiesPriceHistory=");
		builder.append(companiesPriceHistory);
		builder.append("]");
		return builder.toString();
	}
}
