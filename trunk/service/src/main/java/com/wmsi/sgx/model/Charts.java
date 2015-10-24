package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class Charts {

	private double totalRevenue;
	private double grossProfit;
	private double netIncome;
	private double ebitda;
	private double eps;
	private double payoutRatio;
	private double dividendsPerShare;
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
	private double cashOperations;
	private double cashInvesting;
	private double cashFinancing;
	private double netChange;
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
	private double totalRevenue1YrAnnGrowth;
	private double ebitda1YrAnnGrowth;
	private double netIncome1YrAnnGrowth;
	private double eps1YrAnnGrowth;
	private double commonEquity1YrAnnGrowth;
	
	
	@Override
	public int hashCode() {
		return Objects.hashCode(totalRevenue, grossProfit, netIncome, ebitda, eps, payoutRatio, dividendsPerShare,
				totalAssets, totalCurrentAssets, netPpe, totalCurrentLiabily, longTermDebt, totalLiability,
				retainedEarnings, commonStock, totalEquity, minorityInterest, cashOperations, cashInvesting,
				cashFinancing, netChange, returnAssets, returnCapital, returnEquity, grossMargin, ebitdaMargin,
				netIncomeMargin, assetTurns, currentRatio, quickRatio, avgDaysInventory, avgDaysPayable, cashConversion,
				totalDebtEquity, ebitdaInterest, totalRevenue1YrAnnGrowth, ebitda1YrAnnGrowth, netIncome1YrAnnGrowth,
				eps1YrAnnGrowth, commonEquity1YrAnnGrowth);
	}


	@Override
	public boolean equals(Object object) {
		if (object instanceof Charts) {
			Charts that = (Charts) object;
			return Objects.equal(this.totalRevenue, that.totalRevenue)
					&& Objects.equal(this.grossProfit, that.grossProfit)
					&& Objects.equal(this.netIncome, that.netIncome) && Objects.equal(this.ebitda, that.ebitda)
					&& Objects.equal(this.eps, that.eps) && Objects.equal(this.payoutRatio, that.payoutRatio)
					&& Objects.equal(this.dividendsPerShare, that.dividendsPerShare)
					&& Objects.equal(this.totalAssets, that.totalAssets)
					&& Objects.equal(this.totalCurrentAssets, that.totalCurrentAssets)
					&& Objects.equal(this.netPpe, that.netPpe)
					&& Objects.equal(this.totalCurrentLiabily, that.totalCurrentLiabily)
					&& Objects.equal(this.longTermDebt, that.longTermDebt)
					&& Objects.equal(this.totalLiability, that.totalLiability)
					&& Objects.equal(this.retainedEarnings, that.retainedEarnings)
					&& Objects.equal(this.commonStock, that.commonStock)
					&& Objects.equal(this.totalEquity, that.totalEquity)
					&& Objects.equal(this.minorityInterest, that.minorityInterest)
					&& Objects.equal(this.cashOperations, that.cashOperations)
					&& Objects.equal(this.cashInvesting, that.cashInvesting)
					&& Objects.equal(this.cashFinancing, that.cashFinancing)
					&& Objects.equal(this.netChange, that.netChange)
					&& Objects.equal(this.returnAssets, that.returnAssets)
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
					&& Objects.equal(this.ebitdaInterest, that.ebitdaInterest)
					&& Objects.equal(this.totalRevenue1YrAnnGrowth, that.totalRevenue1YrAnnGrowth)
					&& Objects.equal(this.ebitda1YrAnnGrowth, that.ebitda1YrAnnGrowth)
					&& Objects.equal(this.netIncome1YrAnnGrowth, that.netIncome1YrAnnGrowth)
					&& Objects.equal(this.eps1YrAnnGrowth, that.eps1YrAnnGrowth)
					&& Objects.equal(this.commonEquity1YrAnnGrowth, that.commonEquity1YrAnnGrowth);
		}
		return false;
	}


	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("totalRevenue", totalRevenue).add("grossProfit", grossProfit)
				.add("netIncome", netIncome).add("ebitda", ebitda).add("eps", eps).add("payoutRatio", payoutRatio)
				.add("dividendsPerShare", dividendsPerShare).add("totalAssets", totalAssets)
				.add("totalCurrentAssets", totalCurrentAssets).add("netPpe", netPpe)
				.add("totalCurrentLiabily", totalCurrentLiabily).add("longTermDebt", longTermDebt)
				.add("totalLiability", totalLiability).add("retainedEarnings", retainedEarnings)
				.add("commonStock", commonStock).add("totalEquity", totalEquity)
				.add("minorityInterest", minorityInterest).add("cashOperations", cashOperations)
				.add("cashInvesting", cashInvesting).add("cashFinancing", cashFinancing).add("netChange", netChange)
				.add("returnAssets", returnAssets).add("returnCapital", returnCapital).add("returnEquity", returnEquity)
				.add("grossMargin", grossMargin).add("ebitdaMargin", ebitdaMargin)
				.add("netIncomeMargin", netIncomeMargin).add("assetTurns", assetTurns).add("currentRatio", currentRatio)
				.add("quickRatio", quickRatio).add("avgDaysInventory", avgDaysInventory)
				.add("avgDaysPayable", avgDaysPayable).add("cashConversion", cashConversion)
				.add("totalDebtEquity", totalDebtEquity).add("ebitdaInterest", ebitdaInterest)
				.add("totalRevenue1YrAnnGrowth", totalRevenue1YrAnnGrowth).add("ebitda1YrAnnGrowth", ebitda1YrAnnGrowth)
				.add("netIncome1YrAnnGrowth", netIncome1YrAnnGrowth).add("eps1YrAnnGrowth", eps1YrAnnGrowth)
				.add("commonEquity1YrAnnGrowth", commonEquity1YrAnnGrowth).toString();
	}
	
	
}
