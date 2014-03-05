package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class CompanyFinancial{

	private String tickerCode;
	private String period;
	private String absPeriod;
	private Date periodDate;
	private String filingCurrency; 
	private Double totalRevenue;
	private Double gp;
	private Double ni;
	private Double ebitda;
	private Double eps;
	private Double payoutRatio;
	private Double dividendShare;
	private Double totalAssets;
	private Double totalCA;
	private Double nppe;
	private Double totalCL;
	private Double ltDebt;
	private Double totalLiability;
	private Double re;
	private Double common;
	private Double totalEquity;
	private Double minorityInterest;
	private Double cashOper;
	private Double cashInvest;
	private Double cashFinan;
	private Double netChange;
	private Double returnAssets;
	private Double returnCapital;
	private Double returnEquity;
	private Double grossMargin;
	private Double ebitdaMargin;
	private Double niMargin;
	private Double assetTurns;
	private Double currentRatio;
	private Double quickRatio;
	private Double daysInventoryOut;
	private Double daysPayableOut;
	private Double cashConversion;
	private Double ebitDaInt;
	private Double totalDebtEquity;
	private Double totalRevenue1YrAnnGrowth;
	private Double ebitda1YrAnnGrowth;
	private Double ni1YrAnnGrowth;
	private Double eps1YrAnnGrowth;
	private Double commonEquity1YrAnnGrowth;
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getAbsPeriod() {
		return absPeriod;
	}
	public void setAbsPeriod(String absPeriod) {
		this.absPeriod = absPeriod;
	}
	public Date getPeriodDate() {
		return periodDate;
	}
	public void setPeriodDate(Date periodDate) {
		this.periodDate = periodDate;
	}
	public String getFilingCurrency() {
		return filingCurrency;
	}
	public void setFilingCurrency(String filingCurrency) {
		this.filingCurrency = filingCurrency;
	}
	public Double getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public Double getGp() {
		return gp;
	}
	public void setGp(Double gp) {
		this.gp = gp;
	}
	public Double getNi() {
		return ni;
	}
	public void setNi(Double ni) {
		this.ni = ni;
	}
	public Double getEbitda() {
		return ebitda;
	}
	public void setEbitda(Double ebitda) {
		this.ebitda = ebitda;
	}
	public Double getEps() {
		return eps;
	}
	public void setEps(Double eps) {
		this.eps = eps;
	}
	public Double getPayoutRatio() {
		return payoutRatio;
	}
	public void setPayoutRatio(Double payoutRatio) {
		this.payoutRatio = payoutRatio;
	}
	public Double getDividendShare() {
		return dividendShare;
	}
	public void setDividendShare(Double dividendShare) {
		this.dividendShare = dividendShare;
	}
	public Double getTotalAssets() {
		return totalAssets;
	}
	public void setTotalAssets(Double totalAssets) {
		this.totalAssets = totalAssets;
	}
	public Double getTotalCA() {
		return totalCA;
	}
	public void setTotalCA(Double totalCA) {
		this.totalCA = totalCA;
	}
	public Double getNppe() {
		return nppe;
	}
	public void setNppe(Double nppe) {
		this.nppe = nppe;
	}
	public Double getTotalCL() {
		return totalCL;
	}
	public void setTotalCL(Double totalCL) {
		this.totalCL = totalCL;
	}
	public Double getLtDebt() {
		return ltDebt;
	}
	public void setLtDebt(Double ltDebt) {
		this.ltDebt = ltDebt;
	}
	public Double getTotalLiability() {
		return totalLiability;
	}
	public void setTotalLiability(Double totalLiability) {
		this.totalLiability = totalLiability;
	}
	public Double getRe() {
		return re;
	}
	public void setRe(Double re) {
		this.re = re;
	}
	public Double getCommon() {
		return common;
	}
	public void setCommon(Double common) {
		this.common = common;
	}
	public Double getTotalEquity() {
		return totalEquity;
	}
	public void setTotalEquity(Double totalEquity) {
		this.totalEquity = totalEquity;
	}
	public Double getMinorityInterest() {
		return minorityInterest;
	}
	public void setMinorityInterest(Double minorityInterest) {
		this.minorityInterest = minorityInterest;
	}
	public Double getCashOper() {
		return cashOper;
	}
	public void setCashOper(Double cashOper) {
		this.cashOper = cashOper;
	}
	public Double getCashInvest() {
		return cashInvest;
	}
	public void setCashInvest(Double cashInvest) {
		this.cashInvest = cashInvest;
	}
	public Double getCashFinan() {
		return cashFinan;
	}
	public void setCashFinan(Double cashFinan) {
		this.cashFinan = cashFinan;
	}
	public Double getNetChange() {
		return netChange;
	}
	public void setNetChange(Double netChange) {
		this.netChange = netChange;
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
	public Double getGrossMargin() {
		return grossMargin;
	}
	public void setGrossMargin(Double grossMargin) {
		this.grossMargin = grossMargin;
	}
	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}
	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}
	public Double getNiMargin() {
		return niMargin;
	}
	public void setNiMargin(Double niMargin) {
		this.niMargin = niMargin;
	}
	public Double getAssetTurns() {
		return assetTurns;
	}
	public void setAssetTurns(Double assetTurns) {
		this.assetTurns = assetTurns;
	}
	public Double getCurrentRatio() {
		return currentRatio;
	}
	public void setCurrentRatio(Double currentRatio) {
		this.currentRatio = currentRatio;
	}
	public Double getQuickRatio() {
		return quickRatio;
	}
	public void setQuickRatio(Double quickRatio) {
		this.quickRatio = quickRatio;
	}
	public Double getDaysInventoryOut() {
		return daysInventoryOut;
	}
	public void setDaysInventoryOut(Double daysInventoryOut) {
		this.daysInventoryOut = daysInventoryOut;
	}
	public Double getDaysPayableOut() {
		return daysPayableOut;
	}
	public void setDaysPayableOut(Double daysPayableOut) {
		this.daysPayableOut = daysPayableOut;
	}
	public Double getCashConversion() {
		return cashConversion;
	}
	public void setCashConversion(Double cashConversion) {
		this.cashConversion = cashConversion;
	}
	public Double getEbitDaInt() {
		return ebitDaInt;
	}
	public void setEbitDaInt(Double ebitDaInt) {
		this.ebitDaInt = ebitDaInt;
	}
	public Double getTotalDebtEquity() {
		return totalDebtEquity;
	}
	public void setTotalDebtEquity(Double totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
	}
	public Double getTotalRevenue1YrAnnGrowth() {
		return totalRevenue1YrAnnGrowth;
	}
	public void setTotalRevenue1YrAnnGrowth(Double totalRevenue1YrAnnGrowth) {
		this.totalRevenue1YrAnnGrowth = totalRevenue1YrAnnGrowth;
	}
	public Double getEbitda1YrAnnGrowth() {
		return ebitda1YrAnnGrowth;
	}
	public void setEbitda1YrAnnGrowth(Double ebitda1YrAnnGrowth) {
		this.ebitda1YrAnnGrowth = ebitda1YrAnnGrowth;
	}
	public Double getNi1YrAnnGrowth() {
		return ni1YrAnnGrowth;
	}
	public void setNi1YrAnnGrowth(Double ni1YrAnnGrowth) {
		this.ni1YrAnnGrowth = ni1YrAnnGrowth;
	}
	public Double getEps1YrAnnGrowth() {
		return eps1YrAnnGrowth;
	}
	public void setEps1YrAnnGrowth(Double eps1YrAnnGrowth) {
		this.eps1YrAnnGrowth = eps1YrAnnGrowth;
	}
	public Double getCommonEquity1YrAnnGrowth() {
		return commonEquity1YrAnnGrowth;
	}
	public void setCommonEquity1YrAnnGrowth(Double commonEquity1YrAnnGrowth) {
		this.commonEquity1YrAnnGrowth = commonEquity1YrAnnGrowth;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("absPeriod", absPeriod)
			.add("periodDate", periodDate)
			.add("filingCurrency", filingCurrency)
			.add("totalRevenue", totalRevenue)
			.add("gp", gp)
			.add("ni", ni)
			.add("ebitda", ebitda)
			.add("eps", eps)
			.add("payoutRatio", payoutRatio)
			.add("dividendShare", dividendShare)
			.add("totalAssets", totalAssets)
			.add("totalCA", totalCA)
			.add("nppe", nppe)
			.add("totalCL", totalCL)
			.add("ltDebt", ltDebt)
			.add("totalLiability", totalLiability)
			.add("re", re)
			.add("common", common)
			.add("totalEquity", totalEquity)
			.add("minorityInterest", minorityInterest)
			.add("cashOper", cashOper)
			.add("cashInvest", cashInvest)
			.add("cashFinan", cashFinan)
			.add("netChange", netChange)
			.add("returnAssets", returnAssets)
			.add("returnCapital", returnCapital)
			.add("returnEquity", returnEquity)
			.add("grossMargin", grossMargin)
			.add("ebitdaMargin", ebitdaMargin)
			.add("niMargin", niMargin)
			.add("assetTurns", assetTurns)
			.add("currentRatio", currentRatio)
			.add("quickRatio", quickRatio)
			.add("daysInventoryOut", daysInventoryOut)
			.add("daysPayableOut", daysPayableOut)
			.add("cashConversion", cashConversion)
			.add("ebitDaInt", ebitDaInt)
			.add("totalDebtEquity", totalDebtEquity)
			.add("totalRevenue1YrAnnGrowth", totalRevenue1YrAnnGrowth)
			.add("ebitda1YrAnnGrowth", ebitda1YrAnnGrowth)
			.add("ni1YrAnnGrowth", ni1YrAnnGrowth)
			.add("eps1YrAnnGrowth", eps1YrAnnGrowth)
			.add("commonEquity1YrAnnGrowth", commonEquity1YrAnnGrowth)
			.toString();
	}
	
	
}
