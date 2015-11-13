package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class IncomeStatement {
	private String absPeriod;
	private double totalRevenue;
	private double grossProfit;
	private double netIncome;
	private double ebitda;
	private double eps;
	private double payoutRatio;
	private double dividendsPerShare;
	
	
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
	public double getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public double getGrossProfit() {
		return grossProfit;
	}
	public void setGrossProfit(double grossProfit) {
		this.grossProfit = grossProfit;
	}
	public double getNetIncome() {
		return netIncome;
	}
	public void setNetIncome(double netIncome) {
		this.netIncome = netIncome;
	}
	public double getEbitda() {
		return ebitda;
	}
	public void setEbitda(double ebitda) {
		this.ebitda = ebitda;
	}
	public double getEps() {
		return eps;
	}
	public void setEps(double eps) {
		this.eps = eps;
	}
	public double getPayoutRatio() {
		return payoutRatio;
	}
	public void setPayoutRatio(double payoutRatio) {
		this.payoutRatio = payoutRatio;
	}
	public double getDividendsPerShare() {
		return dividendsPerShare;
	}
	public void setDividendsPerShare(double dividendsPerShare) {
		this.dividendsPerShare = dividendsPerShare;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod).add("totalRevenue", totalRevenue)
				.add("grossProfit", grossProfit).add("netIncome", netIncome).add("ebitda", ebitda).add("eps", eps)
				.add("payoutRatio", payoutRatio).add("dividendsPerShare", dividendsPerShare).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, totalRevenue, grossProfit, netIncome, ebitda, eps, payoutRatio,
				dividendsPerShare);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof IncomeStatement) {
			IncomeStatement that = (IncomeStatement) object;
			return Objects.equal(this.absPeriod, that.absPeriod) && Objects.equal(this.totalRevenue, that.totalRevenue)
					&& Objects.equal(this.grossProfit, that.grossProfit)
					&& Objects.equal(this.netIncome, that.netIncome) && Objects.equal(this.ebitda, that.ebitda)
					&& Objects.equal(this.eps, that.eps) && Objects.equal(this.payoutRatio, that.payoutRatio)
					&& Objects.equal(this.dividendsPerShare, that.dividendsPerShare);
		}
		return false;
	}
	
	
}
