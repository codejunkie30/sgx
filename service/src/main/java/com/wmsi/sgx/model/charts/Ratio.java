package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class Ratio {
	private String absPeriod;
	private double returnAssets;
	private double returnCapital;
	private double returnEquity;
	private double grossMargin;
	private double ebitdaMargin;
	private double netIncomeMargin;
	private double assetTurns;
	private double currentRatio;
	private double quickRatio;
	private double avgDaysInventory;
	private double avgDaysPayable;
	private double cashConversion;
	private double totalDebtEquity;
	private double ebitdaInterest;
	
	
	
	
	public String getAbsPeriod() {
		return absPeriod;
	}


	public void setAbsPeriod(String absPeriod) {
		if(absPeriod.substring(0,3).equalsIgnoreCase("LTM")){
			this.absPeriod = absPeriod.substring(0,3) + (absPeriod.substring(absPeriod.length()-4));
		}
		else 
			this.absPeriod=absPeriod;
	}


	public double getReturnAssets() {
		return returnAssets;
	}


	public void setReturnAssets(double returnAssets) {
		this.returnAssets = returnAssets;
	}


	public double getReturnCapital() {
		return returnCapital;
	}


	public void setReturnCapital(double returnCapital) {
		this.returnCapital = returnCapital;
	}


	public double getReturnEquity() {
		return returnEquity;
	}


	public void setReturnEquity(double returnEquity) {
		this.returnEquity = returnEquity;
	}


	public double getGrossMargin() {
		return grossMargin;
	}


	public void setGrossMargin(double grossMargin) {
		this.grossMargin = grossMargin;
	}


	public double getEbitdaMargin() {
		return ebitdaMargin;
	}


	public void setEbitdaMargin(double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}


	public double getNetIncomeMargin() {
		return netIncomeMargin;
	}


	public void setNetIncomeMargin(double netIncomeMargin) {
		this.netIncomeMargin = netIncomeMargin;
	}


	public double getAssetTurns() {
		return assetTurns;
	}


	public void setAssetTurns(double assetTurns) {
		this.assetTurns = assetTurns;
	}


	public double getCurrentRatio() {
		return currentRatio;
	}


	public void setCurrentRatio(double currentRatio) {
		this.currentRatio = currentRatio;
	}


	public double getQuickRatio() {
		return quickRatio;
	}


	public void setQuickRatio(double quickRatio) {
		this.quickRatio = quickRatio;
	}


	public double getAvgDaysInventory() {
		return avgDaysInventory;
	}


	public void setAvgDaysInventory(double avgDaysInventory) {
		this.avgDaysInventory = avgDaysInventory;
	}


	public double getAvgDaysPayable() {
		return avgDaysPayable;
	}


	public void setAvgDaysPayable(double avgDaysPayable) {
		this.avgDaysPayable = avgDaysPayable;
	}


	public double getCashConversion() {
		return cashConversion;
	}


	public void setCashConversion(double cashConversion) {
		this.cashConversion = cashConversion;
	}


	public double getTotalDebtEquity() {
		return totalDebtEquity;
	}


	public void setTotalDebtEquity(double totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
	}


	public double getEbitdaInterest() {
		return ebitdaInterest;
	}


	public void setEbitdaInterest(double ebitdaInterest) {
		this.ebitdaInterest = ebitdaInterest;
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod).add("returnAssets", returnAssets)
				.add("returnCapital", returnCapital).add("returnEquity", returnEquity).add("grossMargin", grossMargin)
				.add("ebitdaMargin", ebitdaMargin).add("netIncomeMargin", netIncomeMargin).add("assetTurns", assetTurns)
				.add("currentRatio", currentRatio).add("quickRatio", quickRatio)
				.add("avgDaysInventory", avgDaysInventory).add("avgDaysPayable", avgDaysPayable)
				.add("cashConversion", cashConversion).add("totalDebtEquity", totalDebtEquity)
				.add("ebitdaInterest", ebitdaInterest).toString();
	}


	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, returnAssets, returnCapital, returnEquity, grossMargin, ebitdaMargin,
				netIncomeMargin, assetTurns, currentRatio, quickRatio, avgDaysInventory, avgDaysPayable, cashConversion,
				totalDebtEquity, ebitdaInterest);
	}


	@Override
	public boolean equals(Object object) {
		if (object instanceof Ratio) {
			Ratio that = (Ratio) object;
			return Objects.equal(this.absPeriod, that.absPeriod) && Objects.equal(this.returnAssets, that.returnAssets)
					&& Objects.equal(this.returnCapital, that.returnCapital)
					&& Objects.equal(this.returnEquity, that.returnEquity)
					&& Objects.equal(this.grossMargin, that.grossMargin)
					&& Objects.equal(this.ebitdaMargin, that.ebitdaMargin)
					&& Objects.equal(this.netIncomeMargin, that.netIncomeMargin)
					&& Objects.equal(this.assetTurns, that.assetTurns)
					&& Objects.equal(this.currentRatio, that.currentRatio)
					&& Objects.equal(this.quickRatio, that.quickRatio)
					&& Objects.equal(this.avgDaysInventory, that.avgDaysInventory)
					&& Objects.equal(this.avgDaysPayable, that.avgDaysPayable)
					&& Objects.equal(this.cashConversion, that.cashConversion)
					&& Objects.equal(this.totalDebtEquity, that.totalDebtEquity)
					&& Objects.equal(this.ebitdaInterest, that.ebitdaInterest);
		}
		return false;
	}
}
