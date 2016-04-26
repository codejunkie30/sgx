package com.wmsi.sgx.model.charts;

import com.google.common.base.Objects;

public class GrowthOverPriorYear {
	private String absPeriod;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	private double ebitda1YrAnnGrowth;
	private double netIncome1YrAnnGrowth;
	private double eps1YrAnnGrowth;
	private double commonEquity1YrAnnGrowth;
	
	
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
				.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth).add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
				.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth).add("ebitda1YrAnnGrowth", ebitda1YrAnnGrowth)
				.add("netIncome1YrAnnGrowth", netIncome1YrAnnGrowth).add("eps1YrAnnGrowth", eps1YrAnnGrowth)
				.add("commonEquity1YrAnnGrowth", commonEquity1YrAnnGrowth).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(absPeriod, totalRev1YrAnnGrowth, totalRev3YrAnnGrowth, totalRev5YrAnnGrowth,
				ebitda1YrAnnGrowth, netIncome1YrAnnGrowth, eps1YrAnnGrowth, commonEquity1YrAnnGrowth);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof GrowthOverPriorYear) {
			GrowthOverPriorYear that = (GrowthOverPriorYear) object;
			return Objects.equal(this.absPeriod, that.absPeriod)
					&& Objects.equal(this.totalRev1YrAnnGrowth, that.totalRev1YrAnnGrowth)
					&& Objects.equal(this.totalRev3YrAnnGrowth, that.totalRev3YrAnnGrowth)
					&& Objects.equal(this.totalRev5YrAnnGrowth, that.totalRev5YrAnnGrowth)
					&& Objects.equal(this.ebitda1YrAnnGrowth, that.ebitda1YrAnnGrowth)
					&& Objects.equal(this.netIncome1YrAnnGrowth, that.netIncome1YrAnnGrowth)
					&& Objects.equal(this.eps1YrAnnGrowth, that.eps1YrAnnGrowth)
					&& Objects.equal(this.commonEquity1YrAnnGrowth, that.commonEquity1YrAnnGrowth);
		}
		return false;
	}
	
	
	
	
}
