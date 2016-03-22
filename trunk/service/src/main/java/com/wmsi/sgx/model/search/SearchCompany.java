package com.wmsi.sgx.model.search;

import java.util.Date;

import com.google.common.base.Objects;

public class SearchCompany{

	private Double avgBrokerReq;
	private Double avgTradedVolM3;
	private Double avgVolumeM3;
	private Double basicEpsIncl;
	private Double beta5Yr;
	private String companyName;
	private Double dividendYield;
	private String exchange;
	private Double ebitdaMargin;
	private Double eps;
	private Date filingDate;
	private Double floatPercentage;
	private Integer gtiScore;
	private Integer gtiRankChange;
	private String industry;
	private String industryGroup;
	private Double marketCap;
	private Double netProfitMargin;
	private Double peRatio;
	private Double percentChange;
	private Double priceToBookRatio;
	private Double priceVolHistYr;
	private Double priceVs52WeekHigh;
	private Double priceVs52WeekLow;
	private Double returnOnEquity;
	private Double targetPriceNum;
	private String tickerCode;
	private Double totalDebtEquity;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	private Double totalRevenue;
	private Double volWeightedAvgPrice;
	private String vwapCurrency;
	private Date vwapAsOfDate;
	private Double volume;
	private String companyOrTicker;

	
	public String getCompanyOrTicker() {
		return companyOrTicker;
	}

	public void setCompanyOrTicker(String companyOrTicker) {
		this.companyOrTicker = companyOrTicker;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public Double getAvgBrokerReq() {
		return avgBrokerReq;
	}

	public void setAvgBrokerReq(Double avgBrokerReq) {
		this.avgBrokerReq = avgBrokerReq;
	}

	public Double getAvgTradedVolM3() {
		return avgTradedVolM3;
	}

	public void setAvgTradedVolM3(Double avgTradedVolM3) {
		this.avgTradedVolM3 = avgTradedVolM3;
	}

	public Double getAvgVolumeM3() {
		return avgVolumeM3;
	}

	public void setAvgVolumeM3(Double avgVolumeM3) {
		this.avgVolumeM3 = avgVolumeM3;
	}

	public Double getBasicEpsIncl() {
		return basicEpsIncl;
	}

	public void setBasicEpsIncl(Double basicEpsIncl) {
		this.basicEpsIncl = basicEpsIncl;
	}

	public Double getBeta5Yr() {
		return beta5Yr;
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

	public Double getFloatPercentage() {
		return floatPercentage;
	}

	public void setFloatPercentage(Double floatPercentage) {
		this.floatPercentage = floatPercentage;
	}

	public Integer getGtiScore() {
		return gtiScore;
	}

	public Integer getGtiRankChange() {
		return gtiRankChange;
	}

	public void setGtiScore(Integer gtiScore) {
		this.gtiScore = gtiScore;
	}

	public void setGtiRankChange(Integer gtiRankChange) {
		this.gtiRankChange = gtiRankChange;
	}

	public String getIndustry() {
		if(exchange == "SGX" || exchange == "CATALIST")
			return null;
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

	public Double getPeRatio() {
		return peRatio;
	}

	public Double getPercentChange() {
		return percentChange;
	}

	public Double getPriceToBookRatio() {
		return priceToBookRatio;
	}

	public Double getPriceVs52WeekHigh() {
		return priceVs52WeekHigh;
	}

	public Double getPriceVs52WeekLow() {
		return priceVs52WeekLow;
	}

	public Double getTargetPriceNum() {
		return targetPriceNum;
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

	public void setBeta5Yr(Double beta5Yr) {
		this.beta5Yr = beta5Yr;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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

	public Date getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
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

	public void setPeRatio(Double peRatio) {
		if(peRatio > 0){
			this.peRatio = peRatio;
		}else
			this.peRatio = null;
		
	}

	public void setPercentChange(Double percentChange) {
		this.percentChange = percentChange;
	}

	public Double getPriceVolHistYr() {
		return priceVolHistYr;
	}

	public void setPriceVolHistYr(Double priceVolHistYr) {
		this.priceVolHistYr = priceVolHistYr;
	}

	public void setPriceToBookRatio(Double priceToBookRatio) {
		this.priceToBookRatio = priceToBookRatio;
	}

	public void setPriceVs52WeekHigh(Double priceVs52WeekHigh) {
		this.priceVs52WeekHigh = priceVs52WeekHigh;
	}

	public void setPriceVs52WeekLow(Double priceVs52WeekLow) {
		this.priceVs52WeekLow = priceVs52WeekLow;
	}

	public Double getReturnOnEquity() {
		return returnOnEquity;
	}

	public void setReturnOnEquity(Double returnOnEquity) {
		this.returnOnEquity = returnOnEquity;
	}

	public void setTargetPriceNum(Double targetPriceNum) {
		this.targetPriceNum = targetPriceNum;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
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

	public String getVwapCurrency() {
		return vwapCurrency;
	}

	public void setVwapCurrency(String vwapCurrency) {
		this.vwapCurrency = vwapCurrency;
	}

	public Date getVwapAsOfDate() {
		return vwapAsOfDate;
	}

	public void setVwapAsOfDate(Date vwapAsOfDate) {
		this.vwapAsOfDate = vwapAsOfDate;
	}

	public Double getVolWeightedAvgPrice() {
		return volWeightedAvgPrice;
	}

	public void setVolWeightedAvgPrice(Double volWeightedAvgPrice) {
		this.volWeightedAvgPrice = volWeightedAvgPrice;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("avgBrokerReq", avgBrokerReq).add("avgTradedVolM3", avgTradedVolM3)
				.add("avgVolumeM3", avgVolumeM3).add("basicEpsIncl", basicEpsIncl).add("beta5Yr", beta5Yr)
				.add("companyName", companyName).add("dividendYield", dividendYield).add("exchange", exchange)
				.add("ebitdaMargin", ebitdaMargin).add("eps", eps).add("filingDate", filingDate)
				.add("floatPercentage", floatPercentage).add("gtiScore", gtiScore).add("gtiRankChange", gtiRankChange)
				.add("industry", industry).add("industryGroup", industryGroup).add("marketCap", marketCap)
				.add("netProfitMargin", netProfitMargin).add("peRatio", peRatio).add("percentChange", percentChange)
				.add("priceToBookRatio", priceToBookRatio).add("priceVolHistYr", priceVolHistYr)
				.add("priceVs52WeekHigh", priceVs52WeekHigh).add("priceVs52WeekLow", priceVs52WeekLow)
				.add("returnOnEquity", returnOnEquity).add("targetPriceNum", targetPriceNum)
				.add("tickerCode", tickerCode).add("totalDebtEquity", totalDebtEquity)
				.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth).add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
				.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth).add("totalRevenue", totalRevenue)
				.add("volWeightedAvgPrice", volWeightedAvgPrice).add("vwapCurrency", vwapCurrency)
				.add("vwapAsOfDate", vwapAsOfDate).add("volume", volume).add("companyOrTicker", companyOrTicker)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(avgBrokerReq, avgTradedVolM3, avgVolumeM3, basicEpsIncl, beta5Yr, companyName,
				dividendYield, exchange, ebitdaMargin, eps, filingDate, floatPercentage, gtiScore, gtiRankChange,
				industry, industryGroup, marketCap, netProfitMargin, peRatio, percentChange, priceToBookRatio,
				priceVolHistYr, priceVs52WeekHigh, priceVs52WeekLow, returnOnEquity, targetPriceNum, tickerCode,
				totalDebtEquity, totalRev1YrAnnGrowth, totalRev3YrAnnGrowth, totalRev5YrAnnGrowth, totalRevenue,
				volWeightedAvgPrice, vwapCurrency, vwapAsOfDate, volume, companyOrTicker);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SearchCompany) {
			SearchCompany that = (SearchCompany) object;
			return Objects.equal(this.avgBrokerReq, that.avgBrokerReq)
					&& Objects.equal(this.avgTradedVolM3, that.avgTradedVolM3)
					&& Objects.equal(this.avgVolumeM3, that.avgVolumeM3)
					&& Objects.equal(this.basicEpsIncl, that.basicEpsIncl) && Objects.equal(this.beta5Yr, that.beta5Yr)
					&& Objects.equal(this.companyName, that.companyName)
					&& Objects.equal(this.dividendYield, that.dividendYield)
					&& Objects.equal(this.exchange, that.exchange)
					&& Objects.equal(this.ebitdaMargin, that.ebitdaMargin) && Objects.equal(this.eps, that.eps)
					&& Objects.equal(this.filingDate, that.filingDate)
					&& Objects.equal(this.floatPercentage, that.floatPercentage)
					&& Objects.equal(this.gtiScore, that.gtiScore)
					&& Objects.equal(this.gtiRankChange, that.gtiRankChange)
					&& Objects.equal(this.industry, that.industry)
					&& Objects.equal(this.industryGroup, that.industryGroup)
					&& Objects.equal(this.marketCap, that.marketCap)
					&& Objects.equal(this.netProfitMargin, that.netProfitMargin)
					&& Objects.equal(this.peRatio, that.peRatio)
					&& Objects.equal(this.percentChange, that.percentChange)
					&& Objects.equal(this.priceToBookRatio, that.priceToBookRatio)
					&& Objects.equal(this.priceVolHistYr, that.priceVolHistYr)
					&& Objects.equal(this.priceVs52WeekHigh, that.priceVs52WeekHigh)
					&& Objects.equal(this.priceVs52WeekLow, that.priceVs52WeekLow)
					&& Objects.equal(this.returnOnEquity, that.returnOnEquity)
					&& Objects.equal(this.targetPriceNum, that.targetPriceNum)
					&& Objects.equal(this.tickerCode, that.tickerCode)
					&& Objects.equal(this.totalDebtEquity, that.totalDebtEquity)
					&& Objects.equal(this.totalRev1YrAnnGrowth, that.totalRev1YrAnnGrowth)
					&& Objects.equal(this.totalRev3YrAnnGrowth, that.totalRev3YrAnnGrowth)
					&& Objects.equal(this.totalRev5YrAnnGrowth, that.totalRev5YrAnnGrowth)
					&& Objects.equal(this.totalRevenue, that.totalRevenue)
					&& Objects.equal(this.volWeightedAvgPrice, that.volWeightedAvgPrice)
					&& Objects.equal(this.vwapCurrency, that.vwapCurrency)
					&& Objects.equal(this.vwapAsOfDate, that.vwapAsOfDate) && Objects.equal(this.volume, that.volume)
					&& Objects.equal(this.companyOrTicker, that.companyOrTicker);
		}
		return false;
	}	
}
