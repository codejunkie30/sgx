/**
 * 
 */
package com.wmsi.sgx.model;

/**
 * @author dt84327
 *
 */
public class CompanyPriceHistory {
	
	private String tickerCode;
	private PriceHistory priceHistory;

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
	 * @return the priceHistory
	 */
	public PriceHistory getPriceHistory() {
		return priceHistory;
	}

	/**
	 * @param priceHistory
	 *            the priceHistory to set
	 */
	public void setPriceHistory(PriceHistory priceHistory) {
		this.priceHistory = priceHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyPriceHistory [tickerCode=");
		builder.append(tickerCode);
		builder.append(", priceHistory=");
		builder.append(priceHistory);
		builder.append("]");
		return builder.toString();
	}
}
