package com.wmsi.sgx.model;

import java.util.Date;

public class Company{

	private Double avgBrokerReq;
	private String exchange;
	private Double avgTradedVolM3;
	private Double avgVolumeM3;
	private Double basicEpsIncl;
	private Double beta5Yr;
	private String businessDescription;
	private Double bvShare;
	private Double capitalExpenditures;
	private Double cashInvestments;
	private Double closePrice;
	private String companyAddress;
	private String companyName;
	private String companyWebsite;
	private Double dividendYield;
	private Double ebit;
	private Double ebitda;
	private Double ebitdaMargin;
	private Double employees;
	private Double enterpriseValue;
	private Double eps;
	private Double evEbitData;
	private Date fiscalYearEnd;
	private Date filingDate;
	private String filingCurrency;
	private Double floatPercentage;
	private Integer gtiScore;
	private Integer gtiRankChange;
	private String gvKey;
	private Double highPrice;
	private String industry;
	private String industryGroup;
	private Double lowPrice;
	private Double marketCap;
	private Double netIncome;
	private Double netProfitMargin;
	private Double openPrice;
	private Double peRatio;
	private Date previousCloseDate;
	private Double previousClosePrice;
	private Double priceToBookRatio;
	private Double priceVolHistYr;
	private Double priceVs52WeekHigh;
	private Double priceVs52WeekLow;
	private Double returnOnEquity;
	private Double sharesOutstanding;
	private Double sharesSoldShort;
	private Double targetPrice;
	private Double targetPriceNum;
	private String tickerCode;
	private Double totalAssets;
	private Double totalDebt;
	private Double totalDebtEbitda;
	private Double totalDebtEquity;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	private Double totalRevenue;
	private Double volume;
	private Double volWeightedAvgPrice;
	private Double adjustedVolWeightedAvgPrice;
	private Date vwapAsOfDate;
	private String vwapCurrency;
	private Integer yearFounded;
	private Double yearHigh;
	private Double yearLow;
	private Double volatility;
	
	
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
		if(basicEpsIncl>0){
			this.basicEpsIncl = basicEpsIncl;
		}else{
			this.basicEpsIncl = null;
		}
	}

	public Double getBeta5Yr() {
		return beta5Yr;
	}

	public void setBeta5Yr(Double beta5Yr) {
		this.beta5Yr = beta5Yr;
	}

	public String getBusinessDescription() {
		return businessDescription;
	}

	public void setBusinessDescription(String businessDescription) {
		this.businessDescription = businessDescription;
	}

	public Double getBvShare() {
		return bvShare;
	}

	public void setBvShare(Double bvShare) {
		this.bvShare = bvShare;
	}

	public Double getCapitalExpenditures() {
		return capitalExpenditures;
	}

	public void setCapitalExpenditures(Double capitalExpenditures) {
		this.capitalExpenditures = capitalExpenditures;
	}

	public Double getCashInvestments() {
		return cashInvestments;
	}

	public void setCashInvestments(Double cashInvestments) {
		this.cashInvestments = cashInvestments;
	}

	public Double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyWebsite() {
		return companyWebsite;
	}

	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}

	public Double getDividendYield() {
		return dividendYield;
	}

	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}

	public Double getEbit() {
		return ebit;
	}

	public void setEbit(Double ebit) {
		this.ebit = ebit;
	}

	public Double getEbitda() {
		return ebitda;
	}

	public void setEbitda(Double ebitda) {
		this.ebitda = ebitda;
	}

	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}

	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}

	public Double getEmployees() {
		return employees;
	}

	public void setEmployees(Double employees) {
		this.employees = employees;
	}

	public Double getEnterpriseValue() {
		return enterpriseValue;
	}

	public void setEnterpriseValue(Double enterpriseValue) {
		this.enterpriseValue = enterpriseValue;
	}

	public Double getEps() {
		return eps;
	}

	public void setEps(Double eps) {
		if (eps > 0) {
			this.eps = eps;
		} else
			this.eps = null;
	}

	public Double getEvEbitData() {
		return evEbitData;
	}

	public void setEvEbitData(Double evEbitData) {
		if(evEbitData > 0){
			this.evEbitData = evEbitData;
		}else this.evEbitData = null;
	}

	public Date getFiscalYearEnd() {
		return fiscalYearEnd;
	}

	public void setFiscalYearEnd(Date fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
	}

	public Date getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}

	public String getFilingCurrency() {
		return filingCurrency;
	}

	public void setFilingCurrency(String filingCurrency) {
		this.filingCurrency = filingCurrency;
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

	public String getGvKey() {
		return gvKey;
	}

	public void setGvKey(String gvKey) {
		this.gvKey = gvKey;
	}

	public Double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
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

	public Double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Double getMarketCap() {
		return marketCap;
	}

	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}

	public Double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(Double netIncome) {
		this.netIncome = netIncome;
	}

	public Double getNetProfitMargin() {
		return netProfitMargin;
	}

	public void setNetProfitMargin(Double netProfitMargin) {
		this.netProfitMargin = netProfitMargin;
	}

	public Double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}

	public Double getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(Double peRatio) {
		if(peRatio > 0){
			this.peRatio = peRatio;
		}else
			this.peRatio = null;
		}

	public Date getPreviousCloseDate() {
		return previousCloseDate;
	}

	public void setPreviousCloseDate(Date previousCloseDate) {
		this.previousCloseDate = previousCloseDate;
	}

	public Double getPreviousClosePrice() {
		return previousClosePrice;
	}

	public void setPreviousClosePrice(Double previousClosePrice) {
		this.previousClosePrice = previousClosePrice;
	}

	public Double getPriceToBookRatio() {
		return priceToBookRatio;
	}

	public void setPriceToBookRatio(Double priceToBookRatio) {
		this.priceToBookRatio = priceToBookRatio;
	}

	public Double getPriceVolHistYr() {
		return priceVolHistYr;
	}

	public void setPriceVolHistYr(Double priceVolHistYr) {
		this.priceVolHistYr = priceVolHistYr;
	}

	public Double getPriceVs52WeekHigh() {
		return priceVs52WeekHigh;
	}

	public void setPriceVs52WeekHigh(Double priceVs52WeekHigh) {
		this.priceVs52WeekHigh = priceVs52WeekHigh;
	}

	public Double getPriceVs52WeekLow() {
		return priceVs52WeekLow;
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

	public Double getSharesOutstanding() {
		return sharesOutstanding;
	}

	public void setSharesOutstanding(Double sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}

	public Double getSharesSoldShort() {
		return sharesSoldShort;
	}

	public void setSharesSoldShort(Double sharesSoldShort) {
		this.sharesSoldShort = sharesSoldShort;
	}

	public Double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public Double getTargetPriceNum() {
		return targetPriceNum;
	}

	public void setTargetPriceNum(Double targetPriceNum) {
		this.targetPriceNum = targetPriceNum;
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

	public Double getTotalDebt() {
		return totalDebt;
	}

	public void setTotalDebt(Double totalDebt) {
		this.totalDebt = totalDebt;
	}

	public Double getTotalDebtEbitda() {
		return totalDebtEbitda;
	}

	public void setTotalDebtEbitda(Double totalDebtEbitda) {
		this.totalDebtEbitda = totalDebtEbitda;
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
		if(totalRev1YrAnnGrowth > 0){
			this.totalRev1YrAnnGrowth = totalRev1YrAnnGrowth;
		}else{
			this.totalRev1YrAnnGrowth = null;
		}
		
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

	public Double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public Date getVwapAsOfDate() {
		return vwapAsOfDate;
	}

	public void setVwapAsOfDate(Date vwapAsOfDate) {
		this.vwapAsOfDate = vwapAsOfDate;
	}

	public String getVwapCurrency() {
		return vwapCurrency;
	}

	public void setVwapCurrency(String vwapCurrency) {
		this.vwapCurrency = vwapCurrency;
	}

	public Double getVolWeightedAvgPrice() {
		return volWeightedAvgPrice;
	}

	public void setVolWeightedAvgPrice(Double volWeightedAvgPrice) {
		this.volWeightedAvgPrice = volWeightedAvgPrice;
	}

	public Double getAdjustedVolWeightedAvgPrice() {
		return adjustedVolWeightedAvgPrice;
	}

	public void setAdjustedVolWeightedAvgPrice(Double adjustedVolWeightedAvgPrice) {
		this.adjustedVolWeightedAvgPrice = adjustedVolWeightedAvgPrice;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Integer getYearFounded() {
		return yearFounded;
	}

	public void setYearFounded(Integer yearFounded) {
		this.yearFounded = yearFounded;
	}

	public Double getYearHigh() {
		return yearHigh;
	}

	public void setYearHigh(Double yearHigh) {
		this.yearHigh = yearHigh;
	}

	public Double getYearLow() {
		return yearLow;
	}

	public void setYearLow(Double yearLow) {
		this.yearLow = yearLow;
	}

	public Double getVolatility() {
		return volatility;
	}

	public void setVolatility(Double volatility) {
		this.volatility = volatility;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		result = prime * result + ((tickerCode == null) ? 0 : tickerCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (exchange == null) {
			if (other.exchange != null)
				return false;
		} else if (!exchange.equals(other.exchange))
			return false;
		if (tickerCode == null) {
			if (other.tickerCode != null)
				return false;
		} else if (!tickerCode.equals(other.tickerCode))
			return false;
		return true;
	}

	
	
}
