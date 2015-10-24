package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class BalanceSheet {
	private String absPeriod;
	private double totalAssets;
	private double totalCurrentAssets;
	private double netPpe;
	private double totalCurrentLiabily;
	private double longTermDebt;
	private double totalLiability;
	private double retainedEarnings;
	private double commonStock;
	private double totalEquity;
	private double minorityInterest;
	
	
	public String getAbsPeriod() {
		return absPeriod;
	}


	public void setAbsPeriod(String absPeriod) {
		this.absPeriod = absPeriod;
	}


	public double getTotalAssets() {
		return totalAssets;
	}


	public void setTotalAssets(double totalAssets) {
		this.totalAssets = totalAssets;
	}


	public double getTotalCurrentAssets() {
		return totalCurrentAssets;
	}


	public void setTotalCurrentAssets(double totalCurrentAssets) {
		this.totalCurrentAssets = totalCurrentAssets;
	}


	public double getNetPpe() {
		return netPpe;
	}


	public void setNetPpe(double netPpe) {
		this.netPpe = netPpe;
	}


	public double getTotalCurrentLiabily() {
		return totalCurrentLiabily;
	}


	public void setTotalCurrentLiabily(double totalCurrentLiabily) {
		this.totalCurrentLiabily = totalCurrentLiabily;
	}


	public double getLongTermDebt() {
		return longTermDebt;
	}


	public void setLongTermDebt(double longTermDebt) {
		this.longTermDebt = longTermDebt;
	}


	public double getTotalLiability() {
		return totalLiability;
	}


	public void setTotalLiability(double totalLiability) {
		this.totalLiability = totalLiability;
	}


	public double getRetainedEarnings() {
		return retainedEarnings;
	}


	public void setRetainedEarnings(double retainedEarnings) {
		this.retainedEarnings = retainedEarnings;
	}


	public double getCommonStock() {
		return commonStock;
	}


	public void setCommonStock(double commonStock) {
		this.commonStock = commonStock;
	}


	public double getTotalEquity() {
		return totalEquity;
	}


	public void setTotalEquity(double totalEquity) {
		this.totalEquity = totalEquity;
	}


	public double getMinorityInterest() {
		return minorityInterest;
	}


	public void setMinorityInterest(double minorityInterest) {
		this.minorityInterest = minorityInterest;
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod).add("totalAssets", totalAssets)
				.add("totalCurrentAssets", totalCurrentAssets).add("netPpe", netPpe)
				.add("totalCurrentLiabily", totalCurrentLiabily).add("longTermDebt", longTermDebt)
				.add("totalLiability", totalLiability).add("retainedEarnings", retainedEarnings)
				.add("commonStock", commonStock).add("totalEquity", totalEquity)
				.add("minorityInterest", minorityInterest).toString();
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, totalAssets, totalCurrentAssets, netPpe, totalCurrentLiabily, longTermDebt,
				totalLiability, retainedEarnings, commonStock, totalEquity, minorityInterest);
	}


	@Override
	public boolean equals(Object object) {
		if (object instanceof BalanceSheet) {
			BalanceSheet that = (BalanceSheet) object;
			return Objects.equal(this.absPeriod, that.absPeriod) && Objects.equal(this.totalAssets, that.totalAssets)
					&& Objects.equal(this.totalCurrentAssets, that.totalCurrentAssets)
					&& Objects.equal(this.netPpe, that.netPpe)
					&& Objects.equal(this.totalCurrentLiabily, that.totalCurrentLiabily)
					&& Objects.equal(this.longTermDebt, that.longTermDebt)
					&& Objects.equal(this.totalLiability, that.totalLiability)
					&& Objects.equal(this.retainedEarnings, that.retainedEarnings)
					&& Objects.equal(this.commonStock, that.commonStock)
					&& Objects.equal(this.totalEquity, that.totalEquity)
					&& Objects.equal(this.minorityInterest, that.minorityInterest);
		}
		return false;
	}


	
	
	
	
	
}
