package com.wmsi.sgx.model.histogram;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

//@JsonRootName(value = "companyHistogram")
public class CompanyHistogram{

	private List<Histogram> marketCap;
	private List<Histogram> totalRevenue;
	private List<Histogram> peRatio;
	private List<Histogram> dividendYield;

	public List<Histogram> getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(List<Histogram> totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public List<Histogram> getPeRatio() {
		return peRatio;
	}
	public void setPeRatio(List<Histogram> peRatio) {
		this.peRatio = peRatio;
	}
	public List<Histogram> getDividendYield() {
		return dividendYield;
	}
	public void setDividendYield(List<Histogram> dividendYield) {
		this.dividendYield = dividendYield;
	}
	public List<Histogram>  getMarketCap(){return marketCap;}
	public void setMarketCap(List<Histogram>  m){marketCap = m;}
}
