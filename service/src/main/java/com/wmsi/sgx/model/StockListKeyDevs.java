/**
 * 
 */
package com.wmsi.sgx.model;

/**
 * @author dt84327
 *
 */
public class StockListKeyDevs {
	private String keyDevId;
	private StockListKeyDev stockListKeyDev;

	/**
	 * @return the keyDevId
	 */
	public String getKeyDevId() {
		return keyDevId;
	}

	/**
	 * @param keyDevId
	 *            the keyDevId to set
	 */
	public void setKeyDevId(String keyDevId) {
		this.keyDevId = keyDevId;
	}

	/**
	 * @return the stockListKeyDev
	 */
	public StockListKeyDev getStockListKeyDev() {
		return stockListKeyDev;
	}

	/**
	 * @param stockListKeyDev
	 *            the stockListKeyDev to set
	 */
	public void setStockListKeyDev(StockListKeyDev stockListKeyDev) {
		this.stockListKeyDev = stockListKeyDev;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockListKeyDevs [keyDevId=");
		builder.append(keyDevId);
		builder.append(", stockListKeyDev=");
		builder.append(stockListKeyDev);
		builder.append("]");
		return builder.toString();
	}

}
