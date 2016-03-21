package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.ConversionAnnotation;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.model.annotation.MillionFormatterAnnotation;

public class Financial{

	@ConversionAnnotation(name = "IQ_ABS_PERIOD")
	private String absPeriod;
	
	
	@ConversionAnnotation(name = "IQ_ASSET_TURNS")
	private Double assetTurns;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_BASIC_EPS_INCL")
	private Double basicEpsIncl;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_DAYS_INVENTORY_OUT")
	private Double avgDaysInventory;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_DAYS_PAYABLE_OUT")
	private Double avgDaysPayable;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CASH_CONVERSION")
	private Double cashConversion;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CASH_FINAN")
	private Double cashFinancing;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CASH_INVEST")
	private Double cashInvesting;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CASH_OPER")
	private Double cashOperations;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_COMMON_EQUITY_1YR_ANN_GROWTH")
	private Double commonEquity1YrAnnGrowth;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_COMMON")
	private Double commonStock;
	
	@ConversionAnnotation(name = "IQ_CURRENT_RATIO")
	private Double currentRatio;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_DIV_SHARE")
	private Double dividendsPerShare;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EBITDA")
	private Double ebitda;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EBITDA_1YR_ANN_GROWTH")
	private Double ebitda1YrAnnGrowth;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EBITDA_INT")
	private Double ebitdaInterest;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EBITDA_MARGIN")
	private Double ebitdaMargin;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_DILUT_EPS_EXCL")
	private Double eps;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EPS_1YR_ANN_GROWTH")
	private Double eps1YrAnnGrowth;
	
	@ConversionAnnotation(name = "IQ_FILINGDATE_IS")
	private String filingDate;
	
	@ConversionAnnotation(name = "IQ_FILING_CURRENCY")
	private String filingCurrency;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_GROSS_MARGIN")
	private Double grossMargin;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_GP")
	private Double grossProfit;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_LT_DEBT")
	private Double longTermDebt;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_MINORITY_INTEREST")
	private Double minorityInterest;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NET_CHANGE")
	private Double netChange;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NI")
	private Double netIncome;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NI_1YR_ANN_GROWTH")
	private Double netIncome1YrAnnGrowth;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NI_MARGIN")
	private Double netIncomeMargin;
	
	@ConversionAnnotation(name = "IQ_NPPE")
	private Double netPpe;
	
	@ConversionAnnotation(name = "IQ_PAYOUT_RATIO")
	private Double payoutRatio;
	
	@ConversionAnnotation(name = "IQ_PERIODDATE_IS")
	private Date periodDate;
	
	@ConversionAnnotation(name = "IQ_QUICK_RATIO")
	private Double quickRatio;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_RE")
	private Double retainedEarnings;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_RETURN_ASSETS")
	private Double returnAssets;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_RETURN_CAPITAL")
	private Double returnCapital;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_RETURN_EQUITY")
	private Double returnEquity;
	
	@ConversionAnnotation(name = "IQ_COMPANY_TICKER_NO_EXCH")
	private String tickerCode;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_ASSETS")
	private Double totalAssets;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_CA")
	private Double totalCurrentAssets;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_CL")
	private Double totalCurrentLiabily;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_DEBT_EQUITY")
	private Double totalDebtEquity;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_EQUITY")
	private Double totalEquity;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_LIAB")
	private Double totalLiability;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_REV")
	private Double totalRevenue;
	
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_REV_1YR_ANN_GROWTH")
	private Double totalRev1YrAnnGrowth;
	
	@FXAnnotation
	private Double totalRev3YrAnnGrowth;
	@FXAnnotation
	private Double totalRev5YrAnnGrowth;

	public String getAbsPeriod() {
		return absPeriod;
	}

	public void setAbsPeriod(String absPeriod) {
		this.absPeriod = absPeriod;
	}

	public Double getAssetTurns() {
		return assetTurns;
	}

	public void setAssetTurns(Double assetTurns) {
		this.assetTurns = assetTurns;
	}

	public Double getBasicEpsIncl() {
		return basicEpsIncl;
	}

	public void setBasicEpsIncl(Double basicEpsIncl) {
		this.basicEpsIncl = basicEpsIncl;
	}

	public Double getAvgDaysInventory() {
		return avgDaysInventory;
	}

	public void setAvgDaysInventory(Double avgDaysInventory) {
		this.avgDaysInventory = avgDaysInventory;
	}

	public Double getAvgDaysPayable() {
		return avgDaysPayable;
	}

	public void setAvgDaysPayable(Double avgDaysPayable) {
		this.avgDaysPayable = avgDaysPayable;
	}

	public Double getCashConversion() {
		return cashConversion;
	}

	public void setCashConversion(Double cashConversion) {
		this.cashConversion = cashConversion;
	}

	public Double getCashFinancing() {
		return cashFinancing;
	}

	public void setCashFinancing(Double cashFinancing) {
		this.cashFinancing = cashFinancing;
	}

	public Double getCashInvesting() {
		return cashInvesting;
	}

	public void setCashInvesting(Double cashInvesting) {
		this.cashInvesting = cashInvesting;
	}

	public Double getCashOperations() {
		return cashOperations;
	}

	public void setCashOperations(Double cashOperations) {
		this.cashOperations = cashOperations;
	}

	public Double getCommonEquity1YrAnnGrowth() {
		return commonEquity1YrAnnGrowth;
	}

	public void setCommonEquity1YrAnnGrowth(Double commonEquity1YrAnnGrowth) {
		this.commonEquity1YrAnnGrowth = commonEquity1YrAnnGrowth;
	}

	public Double getCommonStock() {
		return commonStock;
	}

	public void setCommonStock(Double commonStock) {
		this.commonStock = commonStock;
	}

	public Double getCurrentRatio() {
		return currentRatio;
	}

	public void setCurrentRatio(Double currentRatio) {
		this.currentRatio = currentRatio;
	}

	public Double getDividendsPerShare() {
		return dividendsPerShare;
	}

	public void setDividendsPerShare(Double dividendsPerShare) {
		this.dividendsPerShare = dividendsPerShare;
	}

	public Double getEbitda() {
		return ebitda;
	}

	public void setEbitda(Double ebitda) {
		this.ebitda = ebitda;
	}

	public Double getEbitda1YrAnnGrowth() {
		return ebitda1YrAnnGrowth;
	}

	public void setEbitda1YrAnnGrowth(Double ebitda1YrAnnGrowth) {
		this.ebitda1YrAnnGrowth = ebitda1YrAnnGrowth;
	}

	public Double getEbitdaInterest() {
		return ebitdaInterest;
	}

	public void setEbitdaInterest(Double ebitdaInterest) {
		this.ebitdaInterest = ebitdaInterest;
	}

	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}

	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}

	public Double getEps() {
		return eps;
	}

	public void setEps(Double eps) {
		this.eps = eps;
	}

	public Double getEps1YrAnnGrowth() {
		return eps1YrAnnGrowth;
	}

	public void setEps1YrAnnGrowth(Double eps1YrAnnGrowth) {
		this.eps1YrAnnGrowth = eps1YrAnnGrowth;
	}

	public String getFilingCurrency() {
		return filingCurrency;
	}

	public void setFilingCurrency(String filingCurrency) {
		this.filingCurrency = filingCurrency;
	}

	public String getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(String filingDate) {
		this.filingDate = filingDate;
	}

	public Double getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(Double grossMargin) {
		this.grossMargin = grossMargin;
	}

	public Double getGrossProfit() {
		return grossProfit;
	}

	public void setGrossProfit(Double grossProfit) {
		this.grossProfit = grossProfit;
	}

	public Double getLongTermDebt() {
		return longTermDebt;
	}

	public void setLongTermDebt(Double longTermDebt) {
		this.longTermDebt = longTermDebt;
	}

	public Double getMinorityInterest() {
		return minorityInterest;
	}

	public void setMinorityInterest(Double minorityInterest) {
		this.minorityInterest = minorityInterest;
	}

	public Double getNetChange() {
		return netChange;
	}

	public void setNetChange(Double netChange) {
		this.netChange = netChange;
	}

	public Double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(Double netIncome) {
		this.netIncome = netIncome;
	}

	public Double getNetIncome1YrAnnGrowth() {
		return netIncome1YrAnnGrowth;
	}

	public void setNetIncome1YrAnnGrowth(Double netIncome1YrAnnGrowth) {
		this.netIncome1YrAnnGrowth = netIncome1YrAnnGrowth;
	}

	public Double getNetIncomeMargin() {
		return netIncomeMargin;
	}

	public void setNetIncomeMargin(Double netIncomeMargin) {
		this.netIncomeMargin = netIncomeMargin;
	}

	public Double getNetPpe() {
		return netPpe;
	}

	public void setNetPpe(Double netPpe) {
		this.netPpe = netPpe;
	}

	public Double getPayoutRatio() {
		return payoutRatio;
	}

	public void setPayoutRatio(Double payoutRatio) {
		this.payoutRatio = payoutRatio;
	}

	public Date getPeriodDate() {
		return periodDate;
	}

	public void setPeriodDate(Date periodDate) {
		this.periodDate = periodDate;
	}

	public Double getQuickRatio() {
		return quickRatio;
	}

	public void setQuickRatio(Double quickRatio) {
		this.quickRatio = quickRatio;
	}

	public Double getRetainedEarnings() {
		return retainedEarnings;
	}

	public void setRetainedEarnings(Double retainedEarnings) {
		this.retainedEarnings = retainedEarnings;
	}

	public Double getReturnAssets() {
		return returnAssets;
	}

	public void setReturnAssets(Double returnAssets) {
		this.returnAssets = returnAssets;
	}

	public Double getReturnCapital() {
		return returnCapital;
	}

	public void setReturnCapital(Double returnCapital) {
		this.returnCapital = returnCapital;
	}

	public Double getReturnEquity() {
		return returnEquity;
	}

	public void setReturnEquity(Double returnEquity) {
		this.returnEquity = returnEquity;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public Double getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(Double totalAssets) {
		this.totalAssets = totalAssets;
	}

	public Double getTotalCurrentAssets() {
		return totalCurrentAssets;
	}

	public void setTotalCurrentAssets(Double totalCurrentAssets) {
		this.totalCurrentAssets = totalCurrentAssets;
	}

	public Double getTotalCurrentLiabily() {
		return totalCurrentLiabily;
	}

	public void setTotalCurrentLiabily(Double totalCurrentLiabily) {
		this.totalCurrentLiabily = totalCurrentLiabily;
	}

	public Double getTotalDebtEquity() {
		return totalDebtEquity;
	}

	public void setTotalDebtEquity(Double totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
	}

	public Double getTotalEquity() {
		return totalEquity;
	}

	public void setTotalEquity(Double totalEquity) {
		this.totalEquity = totalEquity;
	}

	public Double getTotalLiability() {
		return totalLiability;
	}

	public void setTotalLiability(Double totalLiability) {
		this.totalLiability = totalLiability;
	}

	public Double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Double getTotalRev1YrAnnGrowth() {
		return totalRev1YrAnnGrowth;
	}

	public void setTotalRev1YrAnnGrowth(Double totalRev1YrAnnGrowth) {
		this.totalRev1YrAnnGrowth = totalRev1YrAnnGrowth;
	}

	public Double getTotalRev3YrAnnGrowth() {
		return totalRev3YrAnnGrowth;
	}

	public void setTotalRev3YrAnnGrowth(Double totalRev3YrAnnGrowth) {
		this.totalRev3YrAnnGrowth = totalRev3YrAnnGrowth;
	}

	public Double getTotalRev5YrAnnGrowth() {
		return totalRev5YrAnnGrowth;
	}

	public void setTotalRev5YrAnnGrowth(Double totalRev5YrAnnGrowth) {
		this.totalRev5YrAnnGrowth = totalRev5YrAnnGrowth;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod).add("assetTurns", assetTurns)
				.add("basicEpsIncl", basicEpsIncl).add("avgDaysInventory", avgDaysInventory)
				.add("avgDaysPayable", avgDaysPayable).add("cashConversion", cashConversion)
				.add("cashFinancing", cashFinancing).add("cashInvesting", cashInvesting)
				.add("cashOperations", cashOperations).add("commonEquity1YrAnnGrowth", commonEquity1YrAnnGrowth)
				.add("commonStock", commonStock).add("currentRatio", currentRatio)
				.add("dividendsPerShare", dividendsPerShare).add("ebitda", ebitda)
				.add("ebitda1YrAnnGrowth", ebitda1YrAnnGrowth).add("ebitdaInterest", ebitdaInterest)
				.add("ebitdaMargin", ebitdaMargin).add("eps", eps).add("eps1YrAnnGrowth", eps1YrAnnGrowth)
				.add("filingDate", filingDate).add("filingCurrency", filingCurrency).add("grossMargin", grossMargin)
				.add("grossProfit", grossProfit).add("longTermDebt", longTermDebt)
				.add("minorityInterest", minorityInterest).add("netChange", netChange).add("netIncome", netIncome)
				.add("netIncome1YrAnnGrowth", netIncome1YrAnnGrowth).add("netIncomeMargin", netIncomeMargin)
				.add("netPpe", netPpe).add("payoutRatio", payoutRatio).add("periodDate", periodDate)
				.add("quickRatio", quickRatio).add("retainedEarnings", retainedEarnings)
				.add("returnAssets", returnAssets).add("returnCapital", returnCapital).add("returnEquity", returnEquity)
				.add("tickerCode", tickerCode).add("totalAssets", totalAssets)
				.add("totalCurrentAssets", totalCurrentAssets).add("totalCurrentLiabily", totalCurrentLiabily)
				.add("totalDebtEquity", totalDebtEquity).add("totalEquity", totalEquity)
				.add("totalLiability", totalLiability).add("totalRevenue", totalRevenue)
				.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth).add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
				.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, assetTurns, basicEpsIncl, avgDaysInventory, avgDaysPayable, cashConversion,
				cashFinancing, cashInvesting, cashOperations, commonEquity1YrAnnGrowth, commonStock, currentRatio,
				dividendsPerShare, ebitda, ebitda1YrAnnGrowth, ebitdaInterest, ebitdaMargin, eps, eps1YrAnnGrowth,
				filingDate, filingCurrency, grossMargin, grossProfit, longTermDebt, minorityInterest, netChange,
				netIncome, netIncome1YrAnnGrowth, netIncomeMargin, netPpe, payoutRatio, periodDate, quickRatio,
				retainedEarnings, returnAssets, returnCapital, returnEquity, tickerCode, totalAssets,
				totalCurrentAssets, totalCurrentLiabily, totalDebtEquity, totalEquity, totalLiability, totalRevenue,
				totalRev1YrAnnGrowth, totalRev3YrAnnGrowth, totalRev5YrAnnGrowth);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Financial) {
			Financial that = (Financial) object;
			return Objects.equal(this.absPeriod, that.absPeriod) && Objects.equal(this.assetTurns, that.assetTurns)
					&& Objects.equal(this.basicEpsIncl, that.basicEpsIncl)
					&& Objects.equal(this.avgDaysInventory, that.avgDaysInventory)
					&& Objects.equal(this.avgDaysPayable, that.avgDaysPayable)
					&& Objects.equal(this.cashConversion, that.cashConversion)
					&& Objects.equal(this.cashFinancing, that.cashFinancing)
					&& Objects.equal(this.cashInvesting, that.cashInvesting)
					&& Objects.equal(this.cashOperations, that.cashOperations)
					&& Objects.equal(this.commonEquity1YrAnnGrowth, that.commonEquity1YrAnnGrowth)
					&& Objects.equal(this.commonStock, that.commonStock)
					&& Objects.equal(this.currentRatio, that.currentRatio)
					&& Objects.equal(this.dividendsPerShare, that.dividendsPerShare)
					&& Objects.equal(this.ebitda, that.ebitda)
					&& Objects.equal(this.ebitda1YrAnnGrowth, that.ebitda1YrAnnGrowth)
					&& Objects.equal(this.ebitdaInterest, that.ebitdaInterest)
					&& Objects.equal(this.ebitdaMargin, that.ebitdaMargin) && Objects.equal(this.eps, that.eps)
					&& Objects.equal(this.eps1YrAnnGrowth, that.eps1YrAnnGrowth)
					&& Objects.equal(this.filingDate, that.filingDate)
					&& Objects.equal(this.filingCurrency, that.filingCurrency)
					&& Objects.equal(this.grossMargin, that.grossMargin)
					&& Objects.equal(this.grossProfit, that.grossProfit)
					&& Objects.equal(this.longTermDebt, that.longTermDebt)
					&& Objects.equal(this.minorityInterest, that.minorityInterest)
					&& Objects.equal(this.netChange, that.netChange) && Objects.equal(this.netIncome, that.netIncome)
					&& Objects.equal(this.netIncome1YrAnnGrowth, that.netIncome1YrAnnGrowth)
					&& Objects.equal(this.netIncomeMargin, that.netIncomeMargin)
					&& Objects.equal(this.netPpe, that.netPpe) && Objects.equal(this.payoutRatio, that.payoutRatio)
					&& Objects.equal(this.periodDate, that.periodDate)
					&& Objects.equal(this.quickRatio, that.quickRatio)
					&& Objects.equal(this.retainedEarnings, that.retainedEarnings)
					&& Objects.equal(this.returnAssets, that.returnAssets)
					&& Objects.equal(this.returnCapital, that.returnCapital)
					&& Objects.equal(this.returnEquity, that.returnEquity)
					&& Objects.equal(this.tickerCode, that.tickerCode)
					&& Objects.equal(this.totalAssets, that.totalAssets)
					&& Objects.equal(this.totalCurrentAssets, that.totalCurrentAssets)
					&& Objects.equal(this.totalCurrentLiabily, that.totalCurrentLiabily)
					&& Objects.equal(this.totalDebtEquity, that.totalDebtEquity)
					&& Objects.equal(this.totalEquity, that.totalEquity)
					&& Objects.equal(this.totalLiability, that.totalLiability)
					&& Objects.equal(this.totalRevenue, that.totalRevenue)
					&& Objects.equal(this.totalRev1YrAnnGrowth, that.totalRev1YrAnnGrowth)
					&& Objects.equal(this.totalRev3YrAnnGrowth, that.totalRev3YrAnnGrowth)
					&& Objects.equal(this.totalRev5YrAnnGrowth, that.totalRev5YrAnnGrowth);
		}
		return false;
	}


}
