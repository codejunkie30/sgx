package com.wmsi.sgx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.base.Objects;

public class CompanyInfo{

	private Double avgBrokerReq;
	private Double beta5Yr;
	private Double bvShare;	
	private Double closePrice;
	private String companyName;
	private Double dividendYield;
	private Double ebitdaMargin;
	private Double eps;
	private String industry;	
	private String industryGroup;
	private Double marketCap;
	private Double netProfitMargin;
	private Double openPrice;
	private Double peRatio;
	private Double previousClosePrice;
	private String tickerCode;
	private Double totalDebtEquity;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	private Double totalRevenue;
	private Double volume;
	private Double yearHigh;
	private Double yearLow;

	public Double getAvgBrokerReq(){
		return avgBrokerReq;
	}
	public Double getBeta5Yr() {
		return beta5Yr;
	}
	public Double getBvShare() {
		return bvShare;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public String getCompanyName() {
		return companyName;
	}
	public Double getDividendYield() {
		return dividendYield;
	}
	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}
	public Double getEps() {
		return eps;
	}
	public String getIndustry() {
		return industry;
	}
	public String getIndustryGroup() {
		return industryGroup;
	}
	public Double getMarketCap() {
		return marketCap;
	}
	public Double getNetProfitMargin() {
		return netProfitMargin;
	}
	public Double getOpenPrice() {
		return openPrice;
	}
	public Double getPeRatio() {
		return peRatio;
	}
	public Double getPreviousClosePrice() {
		return previousClosePrice;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public Double getTotalDebtEquity() {
		return totalDebtEquity;
	}
	public Double getTotalRev1YrAnnGrowth() {
		return totalRev1YrAnnGrowth;
	}
	public Double getTotalRev3YrAnnGrowth() {
		return totalRev3YrAnnGrowth;
	}
	
	public Double getTotalRev5YrAnnGrowth() {
		return totalRev5YrAnnGrowth;
	}
	public Double getTotalRevenue() {
		return totalRevenue;
	}
	public Double getVolume() {
		return volume;
	}
	public Double getYearHigh() {
		return yearHigh;
	}
	
	public Double getYearLow() {
		return yearLow;
	}
	public void setAvgBrokerReq(Double avgBrokerReq) {
		this.avgBrokerReq = avgBrokerReq;
	}
	public void setBeta5Yr(Double beta5Yr) {
		this.beta5Yr = beta5Yr;
	}
	public void setBvShare(Double bvShare) {
		this.bvShare = bvShare;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}
	public void setCompanyName(String name) {
		this.companyName = name;
	}
	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}
	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}
	public void setEps(Double eps) {
		this.eps = eps;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public void setIndustryGroup(String industryGroup) {
		this.industryGroup = industryGroup;
	}
	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}
	
	public void setNetProfitMargin(Double netProfitMargin) {
		this.netProfitMargin = netProfitMargin;
	}
	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}
	public void setPeRatio(Double peRatio) {
		this.peRatio = peRatio;
	}
	public void setPreviousClosePrice(Double previousClosePrice) {
		this.previousClosePrice = previousClosePrice;
	}
	public void setTickerCode(String id) {
		this.tickerCode = id;
	}
	public void setTotalDebtEquity(Double totalDebtEquity) {
		this.totalDebtEquity = totalDebtEquity;
	}
	public void setTotalRev1YrAnnGrowth(Double totalRev1YrAnnGrowth) {
		this.totalRev1YrAnnGrowth = totalRev1YrAnnGrowth;
	}
	public void setTotalRev3YrAnnGrowth(Double totalRev3YrAnnGrowth) {
		this.totalRev3YrAnnGrowth = totalRev3YrAnnGrowth;
	}
	public void setTotalRev5YrAnnGrowth(Double totalRev5YrAnnGrowth) {
		this.totalRev5YrAnnGrowth = totalRev5YrAnnGrowth;
	}
	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}
	
	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public void setYearHigh(Double yearHigh) {
		this.yearHigh = yearHigh;
	}

	public void setYearLow(Double yearLow) {
		this.yearLow = yearLow;
	}

	
	public Double getPriceToBookRatio(){
		
		if(closePrice == null || bvShare == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice); 
		BigDecimal bv = new BigDecimal(bvShare);
		return close.divide(bv, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).doubleValue();
	}
	
	public Double getPriceVs52WeekHigh(){
		
		if(closePrice == null || yearHigh == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice); 
		BigDecimal high = new BigDecimal(yearHigh);
		return close.divide(high, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).doubleValue();
	}
	
	public Double getPriceVs52WeekLow(){
		
		if(closePrice == null || yearLow == null)
			return null;
		
		BigDecimal close = new BigDecimal(closePrice); 
		BigDecimal low = new BigDecimal(yearLow);
		return close.divide(low, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).doubleValue();
	}

	@Override
	public String toString(){
		return Objects.toStringHelper(this)
				.add("tickerCode", tickerCode)
				.add("companyName", companyName)
				.add("marketCap", marketCap)
				.add("closePrice", closePrice)
				.add("previousClosePrice", previousClosePrice)				
				.add("openPrice", openPrice)
				.add("yearHigh", yearHigh)
				.add("yearLow", yearLow)
				.add("industry", industry)
				.add("dividendYield", dividendYield)
				.add("peRatio", peRatio)
				.add("totalRevenue", totalRevenue)
				.add("volume", volume)
				.add("beta5Yr", beta5Yr)				
				.add("industryGroup", industryGroup)
				.add("ebitdaMargin", ebitdaMargin)
				.add("netProfitMargin", netProfitMargin)
				.add("eps", eps)
				.add("totalDebtEquity", totalDebtEquity)
				.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth)
				.add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
				.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth)
				.add("avgBrokerReq", avgBrokerReq)
				.add("bvShare", bvShare)
				.toString();
	}
}
