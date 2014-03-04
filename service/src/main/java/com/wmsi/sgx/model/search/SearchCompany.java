package com.wmsi.sgx.model.search;

public class SearchCompany{

	private String companyName;
	private String code;
	private String industry;
	private Double marketCap;
	private Double totalRevenue;
	public Double getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	private Double peRatio;
	private Double dividendYield;

	public Double getDividendYield() {
		return dividendYield;
	}
	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public Double getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}
	public Double getPeRatio() {
		return peRatio;
	}
	public void setPeRatio(Double peRatio) {
		this.peRatio = peRatio;
	}
}
