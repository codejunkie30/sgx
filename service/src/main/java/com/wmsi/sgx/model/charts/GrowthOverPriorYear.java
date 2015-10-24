package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class GrowthOverPriorYear {
	private String absPeriod;
	private double totalRevenue1YrAnnGrowth;
	private double ebitda1YrAnnGrowth;
	private double netIncome1YrAnnGrowth;
	private double eps1YrAnnGrowth;
	private double commonEquity1YrAnnGrowth;
	
	
	public String getAbsPeriod() {
		return absPeriod;
	}
	public void setAbsPeriod(String absPeriod) {
		this.absPeriod = absPeriod;
	}
	public double getTotalRevenue1YrAnnGrowth() {
		return totalRevenue1YrAnnGrowth;
	}
	public void setTotalRevenue1YrAnnGrowth(double totalRevenue1YrAnnGrowth) {
		this.totalRevenue1YrAnnGrowth = totalRevenue1YrAnnGrowth;
	}
	public double getEbitda1YrAnnGrowth() {
		return ebitda1YrAnnGrowth;
	}
	public void setEbitda1YrAnnGrowth(double ebitda1YrAnnGrowth) {
		this.ebitda1YrAnnGrowth = ebitda1YrAnnGrowth;
	}
	public double getNetIncome1YrAnnGrowth() {
		return netIncome1YrAnnGrowth;
	}
	public void setNetIncome1YrAnnGrowth(double netIncome1YrAnnGrowth) {
		this.netIncome1YrAnnGrowth = netIncome1YrAnnGrowth;
	}
	public double getEps1YrAnnGrowth() {
		return eps1YrAnnGrowth;
	}
	public void setEps1YrAnnGrowth(double eps1YrAnnGrowth) {
		this.eps1YrAnnGrowth = eps1YrAnnGrowth;
	}
	public double getCommonEquity1YrAnnGrowth() {
		return commonEquity1YrAnnGrowth;
	}
	public void setCommonEquity1YrAnnGrowth(double commonEquity1YrAnnGrowth) {
		this.commonEquity1YrAnnGrowth = commonEquity1YrAnnGrowth;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("absPeriod", absPeriod)
				.add("totalRevenue1YrAnnGrowth", totalRevenue1YrAnnGrowth).add("ebitda1YrAnnGrowth", ebitda1YrAnnGrowth)
				.add("netIncome1YrAnnGrowth", netIncome1YrAnnGrowth).add("eps1YrAnnGrowth", eps1YrAnnGrowth)
				.add("commonEquity1YrAnnGrowth", commonEquity1YrAnnGrowth).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, totalRevenue1YrAnnGrowth, ebitda1YrAnnGrowth, netIncome1YrAnnGrowth,
				eps1YrAnnGrowth, commonEquity1YrAnnGrowth);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof GrowthOverPriorYear) {
			GrowthOverPriorYear that = (GrowthOverPriorYear) object;
			return Objects.equal(this.absPeriod, that.absPeriod)
					&& Objects.equal(this.totalRevenue1YrAnnGrowth, that.totalRevenue1YrAnnGrowth)
					&& Objects.equal(this.ebitda1YrAnnGrowth, that.ebitda1YrAnnGrowth)
					&& Objects.equal(this.netIncome1YrAnnGrowth, that.netIncome1YrAnnGrowth)
					&& Objects.equal(this.eps1YrAnnGrowth, that.eps1YrAnnGrowth)
					&& Objects.equal(this.commonEquity1YrAnnGrowth, that.commonEquity1YrAnnGrowth);
		}
		return false;
	}
	
	
	
}
