/**
 * 
 */
package com.wmsi.sgx.model;

/**
 * @author dt84327
 *
 */
public class RSAPubkey {
	
	private String pubKey;

	/**
	 * @return the pubKey
	 */
	public String getPubKey() {
		return pubKey;
	}

	/**
	 * @param pubKey
	 *            the pubKey to set
	 */
	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RSAPubkey [pubKey=");
		builder.append(pubKey);
		builder.append("]");
		return builder.toString();
	}
}
