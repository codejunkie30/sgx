/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.List;

/**
 * @author dt84327
 *
 */
public class StockListKeyDev implements Comparable<StockListKeyDev>{

	private List<String> tickerCodes;
	private String type;
	private Long date;
	private String headline;
	private String situation;
	private String time;
	private String source;
	
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	

	/**
	 * @return the headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * @param headline
	 *            the headline to set
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/**
	 * @return the situation
	 */
	public String getSituation() {
		return situation;
	}

	/**
	 * @param situation
	 *            the situation to set
	 */
	public void setSituation(String situation) {
		this.situation = situation;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StockListKeyDev [tickerCodes=");
		builder.append(tickerCodes);
		builder.append(", type=");
		builder.append(type);
		builder.append(", date=");
		builder.append(date);
		builder.append(", headline=");
		builder.append(headline);
		builder.append(", situation=");
		builder.append(situation);
		builder.append(", time=");
		builder.append(time);
		builder.append(", source=");
		builder.append(source);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(StockListKeyDev o) {
		return this.date.compareTo(o.getDate());
	}

	/**
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
	}
}
