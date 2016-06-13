/**
 * 
 */
package com.wmsi.sgx.model.keydevs;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author dt84327
 *
 */
public class StockListKeyDevsRequest {

	@Valid
	@NotNull(message = "Ticker can not be null")
	private List<String> tickerCodes;

	private Object to;
	private Object from;

	/**
	 * @return the tickerCodes
	 */
	public List<String> getTickerCodes() {
		return tickerCodes;
	}

	/**
	 * @param tickerCodes
	 *            the tickerCodes to set
	 */
	public void setTickerCodes(List<String> tickerCodes) {
		this.tickerCodes = tickerCodes;
	}

	/**
	 * @return the to
	 */
	public Object getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(Object to) {
		this.to = to;
	}

	/**
	 * @return the from
	 */
	public Object getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(Object from) {
		this.from = from;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockListKeyDevsRequest [tickerCodes=");
		builder.append(tickerCodes);
		builder.append(", to=");
		builder.append(to);
		builder.append(", from=");
		builder.append(from);
		builder.append("]");
		return builder.toString();
	}

}
