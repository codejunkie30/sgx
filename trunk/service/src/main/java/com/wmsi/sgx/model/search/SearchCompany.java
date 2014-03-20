package com.wmsi.sgx.model.search;

import com.google.common.base.Objects;


public class SearchCompany{

	private String tickerCode;
	private Double marketCap;
	private Double percentChange;
	private Double priceVs52WeekLow;
	private Double priceVs52WeekHigh;
	private Double volume;
	private Double beta5Yr;
	
	private Double totalRevenue;
	private Double ebitdaMargin;
	private Double netProfitMargin;
	private Double eps;
	private Double totalDebtEquity;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	
	private Double peRatio;
	private Double dividendYield;	
	private Double priceToBookRatio;
	private Double targetPriceNum;
	
	private String industry;
	private String industryGroup;
	

	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	
	public Double getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}
	public Double getPercentChange() {
		return percentChange;
	}
	public void setPercentChange(Double percentChange) {
		this.percentChange = percentChange;
	}
	public Double getPriceVs52WeekLow() {
		return priceVs52WeekLow;
	}
	public void setPriceVs52WeekLow(Double priceVs52WeekLow) {
		this.priceVs52WeekLow = priceVs52WeekLow;
	}
	public Double getPriceVs52WeekHigh() {
		return priceVs52WeekHigh;
	}
	public void setPriceVs52WeekHigh(Double priceVs52WeekHigh) {
		this.priceVs52WeekHigh = priceVs52WeekHigh;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getBeta5Yr() {
		return beta5Yr;
	}
	public void setBeta5Yr(Double beta5Yr) {
		this.beta5Yr = beta5Yr;
	}
	public Double getTotalRevenue() {
		return totalRevenue;
	}
	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}
	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}
	public Double getNetProfitMargin() {
		return netProfitMargin;
	}
	public void setNetProfitMargin(Double netProfitMargin) {
		this.netProfitMargin = netProfitMargin;
	}
	public Double getEps() {
		return eps;
	}
	public void setEps(Double eps) {
		this.eps = eps;
	}
	public Double getTotalDebtEquity() {
		return totalDebtEquity;
	}
	public void setTotalDebtEquity(Double totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
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
	public Double getPeRatio() {
		return peRatio;
	}
	public void setPeRatio(Double peRatio) {
		this.peRatio = peRatio;
	}
	public Double getDividendYield() {
		return dividendYield;
	}
	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}
	public Double getPriceToBookRatio() {
		return priceToBookRatio;
	}
	public void setPriceToBookRatio(Double priceToBookRatio) {
		this.priceToBookRatio = priceToBookRatio;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("marketCap", marketCap)
			.add("percentChange", percentChange)
			.add("priceVs52WeekLow", priceVs52WeekLow)
			.add("priceVs52WeekHigh", priceVs52WeekHigh)
			.add("volume", volume)
			.add("beta5Yr", beta5Yr)
			.add("totalRevenue", totalRevenue)
			.add("ebitdaMargin", ebitdaMargin)
			.add("netProfitMargin", netProfitMargin)
			.add("eps", eps)
			.add("totalDebtEquity", totalDebtEquity)
			.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth)
			.add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
			.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth)
			.add("peRatio", peRatio)
			.add("dividendYield", dividendYield)
			.add("priceToBookRatio", priceToBookRatio)
			.add("targetPriceNum", targetPriceNum)
			.add("industry", industry)
			.add("industryGroup", industryGroup)
			.toString();
	}
	public Double getTargetPriceNum() {
		return targetPriceNum;
	}
	public void setTargetPriceNum(Double targetPriceNum) {
		this.targetPriceNum = targetPriceNum;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getIndustryGroup() {
		return industryGroup;
	}
	public void setIndustryGroup(String industryGroup) {
		this.industryGroup = industryGroup;
	}
	
	
}
