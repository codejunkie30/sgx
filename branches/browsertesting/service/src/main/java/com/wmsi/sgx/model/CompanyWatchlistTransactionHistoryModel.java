/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.Map;

/**
 * @author dt84327
 *
 */
public class CompanyWatchlistTransactionHistoryModel {

	private Double totalInvested = 0.0;

	private Double currentValue;

	private Double percentageChange;

	Map<String, CompanyWatchlistTransactionModel> companies;

	/**
	 * @return the totalInvested
	 */
	public Double getTotalInvested() {
		return totalInvested;
	}

	/**
	 * @param totalInvested
	 *            the totalInvested to set
	 */
	public void setTotalInvested(Double totalInvested) {
		this.totalInvested = totalInvested;
	}

	/**
	 * @return the currentValue
	 */
	public Double getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue
	 *            the currentValue to set
	 */
	public void setCurrentValue(Double currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * @return the percentageChange
	 */
	public Double getPercentageChange() {
		return percentageChange;
	}

	/**
	 * @param percentageChange
	 *            the percentageChange to set
	 */
	public void setPercentageChange(Double percentageChange) {
		this.percentageChange = percentageChange;
	}

	/**
	 * @return the companies
	 */
	public Map<String, CompanyWatchlistTransactionModel> getCompanies() {
		return companies;
	}

	/**
	 * @param companies
	 *            the companies to set
	 */
	public void setCompanies(Map<String, CompanyWatchlistTransactionModel> companies) {
		this.companies = companies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompanyWatchlistTransactionHistoryModel [totalInvested=");
		builder.append(totalInvested);
		builder.append(", currentValue=");
		builder.append(currentValue);
		builder.append(", percentageChange=");
		builder.append(percentageChange);
		builder.append(", companies=");
		builder.append(companies);
		builder.append("]");
		return builder.toString();
	}
}
