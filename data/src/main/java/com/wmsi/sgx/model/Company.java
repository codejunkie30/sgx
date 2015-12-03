package com.wmsi.sgx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.ConversionAnnotation;
import com.wmsi.sgx.model.annotation.FXAnnotation;
import com.wmsi.sgx.model.annotation.MillionFormatterAnnotation;

@JsonRootName("company")
public class Company {

	@ConversionAnnotation(name = "IQ_AVG_BROKER_REC_NO_CIQ")
	private Double avgBrokerReq;
	
	private String exchange;
	
	private Double avgTradedVolM3;
	
	private Double avgVolumeM3;

	@ConversionAnnotation(name = "IQ_BASIC_EPS_INCL")
	private Double basicEpsIncl;

	@ConversionAnnotation(name = "IQ_BETA_5YR")
	private Double beta5Yr;

	@ConversionAnnotation(name = "IQ_BUSINESS_DESCRIPTION")
	private String businessDescription;

	@ConversionAnnotation(name = "IQ_BV_SHARE")
	private Double bvShare;
	
	private String companyId;
	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CAPEX")
	private Double capitalExpenditures;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_CASH_ST_INVEST")
	private Double cashInvestments;

	@ConversionAnnotation(name = "IQ_CLOSEPRICE")
	private Double closePrice;

	@ConversionAnnotation(name = "IQ_COMPANY_ADDRESS")
	private String companyAddress;

	@ConversionAnnotation(name = "IQ_COMPANY_NAME")
	private String companyName;

	@ConversionAnnotation(name = "IQ_COMPANY_WEBSITE")
	private String companyWebsite;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_DIV_SHARE")
	private Double divShare;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_EBIT")
	private Double ebit;

	@ConversionAnnotation(name = "IQ_EBITDA")
	private Double ebitda;

	@ConversionAnnotation(name = "IQ_EBITDA_MARGIN")
	private Double ebitdaMargin;
	
	private Double evEbitData;

	@ConversionAnnotation(name = "IQ_EMPLOYEES")
	private Double employees;

	@ConversionAnnotation(name = "IQ_DILUT_EPS_EXCL")
	private Double eps;

	@ConversionAnnotation(name = "IQ_FILING_CURRENCY")
	private String filingCurrency;

	@ConversionAnnotation(name = "IQ_FILINGDATE_IS")
	private Date filingDate;

	@ConversionAnnotation(name = "IQ_PERIODDATE_IS")
	private Date fiscalYearEnd;

	@ConversionAnnotation(name = "IQ_FLOAT_PERCENT")
	private Double floatPercentage;

	private Integer gtiScore;

	private Integer gtiRankChange;

	@ConversionAnnotation(name = "IQ_GVKEY")
	private String gvKey;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_HIGHPRICE")
	private Double highPrice;

	@ConversionAnnotation(name = "IQ_INDUSTRY")
	private String industry;

	@ConversionAnnotation(name = "IQ_INDUSTRY_GROUP")
	private String industryGroup;

	@ConversionAnnotation(name = "IQ_LASTSALEPRICE")
	private Double lastSalePrice;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_LOWPRICE")
	private Double lowPrice;

	@ConversionAnnotation(name = "IQ_MINORITY_INTEREST")
	private Double minorityInterest;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NET_DEBT")
	private Double netDebt;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_NI")
	private Double netIncome;

	@ConversionAnnotation(name = "IQ_NI_MARGIN")
	private Double netProfitMargin;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_OPENPRICE")
	private Double openPrice;

	@ConversionAnnotation(name = "IQ_PRICEDATE")
	private Date previousCloseDate;

	@ConversionAnnotation(name = "IQ_CLOSE_PRICE")
	private Double previousClosePrice;

	@ConversionAnnotation(name = "IQ_PRICE_VOL_HIST_YR")
	private Double priceVolHistYr;

	@ConversionAnnotation(name = "IQ_RETURN_EQUITY")
	private Double returnOnEquity;

	@ConversionAnnotation(name = "IQ_SHORT_INTEREST")
	private Double sharesSoldShort;
	
	@MillionFormatterAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_OUTSTANDING_FILING_DATE")
	private Double sharesOutstanding;

	@ConversionAnnotation(name = "IQ_TARGET_PRICE_NUM_CIQ")
	private Double targetPriceNum;

	@ConversionAnnotation(name = "IQ_PRICE_TARGET_CIQ")
	private Double targetPrice;

	@ConversionAnnotation(name = "IQ_COMPANY_TICKER_NO_EXCH")
	private String tickerCode;

	@ConversionAnnotation(name = "IQ_TBV")
	private Double tbv;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_ASSETS")
	private Double totalAssets;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_DEBT")
	private Double totalDebt;
	
	
	@ConversionAnnotation(name = "IQ_TOTAL_DEBT_EBITDA")
	private Double totalDebtEbitda;
	
	
	@ConversionAnnotation(name = "IQ_TOTAL_DEBT_EQUITY")
	private Double totalDebtEquity;

	@ConversionAnnotation(name = "IQ_TOTAL_REV_1YR_ANN_GROWTH")
	private Double totalRev1YrAnnGrowth;

	@ConversionAnnotation(name = "IQ_TOTAL_REV_3YR_ANN_GROWTH")
	private Double totalRev3YrAnnGrowth;

	@ConversionAnnotation(name = "IQ_TOTAL_REV_5YR_ANN_GROWTH")
	private Double totalRev5YrAnnGrowth;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_TOTAL_REV")
	private Double totalRevenue;

	private String tradeName;

	@ConversionAnnotation(name = "IQ_VOLUME")
	@MillionFormatterAnnotation
	private Double volume;

	private Date vwapAsOfDate;

	private String vwapCurrency;

	private Double volWeightedAvgPrice;

	@ConversionAnnotation(name = "IQ_YEAR_FOUNDED")
	private Integer yearFounded;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_YEARHIGH")
	private Double yearHigh;

	@FXAnnotation
	@ConversionAnnotation(name = "IQ_YEARLOW")
	private Double yearLow;
	
	private Double volatility;

	private List<HistoricalValue> priceHistory;

	private List<DividendValue> dividendHistory;
	
	private Double peRatio;
	
	@FXAnnotation
	private Double marketCap;
	
	public PriceHistory fullPH;
	
	

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public List<DividendValue> getDividendHistory() {
		return dividendHistory;
	}

	public void setDividendHistory(List<DividendValue> dividendHistory) {
		this.dividendHistory = dividendHistory;
	}

	public List<HistoricalValue> getPriceHistory() {
		return priceHistory;
	}

	public void setPriceHistory(List<HistoricalValue> priceHistory) {
		this.priceHistory = priceHistory;
	}

	public Double getPriceVs52WeekHigh() {

		if (closePrice == null || closePrice == 0 || yearHigh == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice);
		BigDecimal high = new BigDecimal(yearHigh);

		return close.divide(high, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(new BigDecimal(100))
				.doubleValue();
	}
	
	
	public Double getPriceVs52WeekLow() {

		if (closePrice == null || closePrice == 0 || yearLow == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice);
		BigDecimal low = new BigDecimal(yearLow);

		return close.divide(low, RoundingMode.HALF_UP).subtract(BigDecimal.ONE).multiply(new BigDecimal(100))
				.doubleValue();
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

		if (divShare == null || closePrice == null || divShare == 0 || closePrice == 0) return null;

		BigDecimal share = new BigDecimal(divShare);
		BigDecimal close = new BigDecimal(closePrice);
		return share.divide(close, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
	}

	public Double getDivShare() {
		return divShare;
	}

	public void setDivShare(Double divShare) {
		this.divShare = divShare;
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
	
	
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
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
		Double cap = (getMarketCap() != null) ? getMarketCap() : 0;
		Double debt = (netDebt != null) ? netDebt : 0;
		Double interest = (minorityInterest != null) ? minorityInterest : 0;
		return cap + debt + interest;
	}

	public Double getEps() {
		return eps;
	}

	public void setEps(Double eps) {
		this.eps = eps;
	}

	public Double getEvEbitData() {
		return evEbitData;
	}
	
	public void setEvEbitData(Double evEbitData) {
		this.evEbitData = evEbitData;
	}

	public String getFilingCurrency() {
		return filingCurrency;
	}

	public void setFilingCurrency(String filingCurrency) {
		this.filingCurrency = filingCurrency;
	}

	public Date getFilingDate() {
		return filingDate;
	}

	public void setFilingDate(Date filingDate) {
		this.filingDate = filingDate;
	}

	public Date getFiscalYearEnd() {
		return fiscalYearEnd;
	}

	public void setFiscalYearEnd(Date fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
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

	public Double getLastSalePrice() {
		return closePrice;
	}

	public void setLastSalePrice(Double lastSalePrice) {
		this.lastSalePrice = lastSalePrice;
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

	public Double getMinorityInterest() {
		return minorityInterest;
	}

	public void setMinorityInterest(Double minorityInterest) {
		this.minorityInterest = minorityInterest;
	}

	public Double getNetDebt() {
		return netDebt;
	}

	public void setNetDebt(Double netDebt) {
		this.netDebt = netDebt;
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
		this.peRatio = peRatio;
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
		if (closePrice == null || bvShare == null || bvShare == 0) {
			return null;
		} else {
			BigDecimal price = new BigDecimal(closePrice);
			BigDecimal bookValue = new BigDecimal(bvShare);
			return price.divide(bookValue, RoundingMode.HALF_UP).doubleValue();
		}

	}

	public Double getPriceVolHistYr() {
		return priceVolHistYr;
	}

	public void setPriceVolHistYr(Double priceVolHistYr) {
		this.priceVolHistYr = priceVolHistYr;
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

	public Double getTargetPriceNum() {
		return targetPriceNum;
	}

	public void setTargetPriceNum(Double targetPriceNum) {
		this.targetPriceNum = targetPriceNum;
	}

	public Double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public String getTickerCode() {
		return tickerCode;
	}

	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}

	public Double getTbv() {
		return tbv;
	}

	public void setTbv(Double tbv) {
		this.tbv = tbv;
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

	public Double getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Double totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
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
	public String toString() {
		return Objects.toStringHelper(this).add("avgBrokerReq", avgBrokerReq).add("exchange", exchange)
				.add("avgTradedVolM3", avgTradedVolM3).add("avgVolumeM3", avgVolumeM3).add("basicEpsIncl", basicEpsIncl)
				.add("beta5Yr", beta5Yr).add("businessDescription", businessDescription).add("bvShare", bvShare)
				.add("companyId", companyId).add("capitalExpenditures", capitalExpenditures)
				.add("cashInvestments", cashInvestments).add("closePrice", closePrice)
				.add("companyAddress", companyAddress).add("companyName", companyName)
				.add("companyWebsite", companyWebsite).add("divShare", divShare).add("ebit", ebit).add("ebitda", ebitda)
				.add("ebitdaMargin", ebitdaMargin).add("employees", employees).add("eps", eps)
				.add("filingCurrency", filingCurrency).add("filingDate", filingDate).add("fiscalYearEnd", fiscalYearEnd)
				.add("floatPercentage", floatPercentage).add("gtiScore", gtiScore).add("gtiRankChange", gtiRankChange)
				.add("gvKey", gvKey).add("highPrice", highPrice).add("industry", industry)
				.add("industryGroup", industryGroup).add("lastSalePrice", lastSalePrice).add("lowPrice", lowPrice)
				.add("minorityInterest", minorityInterest).add("netDebt", netDebt).add("netIncome", netIncome)
				.add("netProfitMargin", netProfitMargin).add("openPrice", openPrice)
				.add("previousCloseDate", previousCloseDate).add("previousClosePrice", previousClosePrice)
				.add("priceVolHistYr", priceVolHistYr).add("returnOnEquity", returnOnEquity)
				.add("sharesSoldShort", sharesSoldShort).add("sharesOutstanding", sharesOutstanding)
				.add("targetPriceNum", targetPriceNum).add("targetPrice", targetPrice).add("tickerCode", tickerCode)
				.add("tbv", tbv).add("totalAssets", totalAssets).add("totalDebt", totalDebt)
				.add("totalDebtEbitda", totalDebtEbitda).add("totalDebtEquity", totalDebtEquity)
				.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth).add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
				.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth).add("totalRevenue", totalRevenue)
				.add("tradeName", tradeName).add("volume", volume).add("vwapAsOfDate", vwapAsOfDate)
				.add("vwapCurrency", vwapCurrency).add("volWeightedAvgPrice", volWeightedAvgPrice)
				.add("yearFounded", yearFounded).add("yearHigh", yearHigh).add("yearLow", yearLow)
				.add("priceHistory", priceHistory).add("dividendHistory", dividendHistory).add("marketCap", marketCap)
				.add("fullPH", fullPH).add("volatility", volatility).add("peRatio", peRatio).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(avgBrokerReq, exchange, avgTradedVolM3, avgVolumeM3, basicEpsIncl, beta5Yr,
				businessDescription, bvShare, companyId, capitalExpenditures, cashInvestments, closePrice,
				companyAddress, companyName, companyWebsite, divShare, ebit, ebitda, ebitdaMargin, employees, eps,
				filingCurrency, filingDate, fiscalYearEnd, floatPercentage, gtiScore, gtiRankChange, gvKey, highPrice,
				industry, industryGroup, lastSalePrice, lowPrice, minorityInterest, netDebt, netIncome, netProfitMargin,
				openPrice, previousCloseDate, previousClosePrice, priceVolHistYr, returnOnEquity, sharesSoldShort,
				sharesOutstanding, targetPriceNum, targetPrice, tickerCode, tbv, totalAssets, totalDebt,
				totalDebtEbitda, totalDebtEquity, totalRev1YrAnnGrowth, totalRev3YrAnnGrowth, totalRev5YrAnnGrowth,
				totalRevenue, tradeName, volume, vwapAsOfDate, vwapCurrency, volWeightedAvgPrice, yearFounded, yearHigh,
				yearLow, priceHistory, dividendHistory, marketCap, fullPH, volatility, peRatio);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Company) {
			Company that = (Company) object;
			return Objects.equal(this.avgBrokerReq, that.avgBrokerReq) && Objects.equal(this.exchange, that.exchange)
					&& Objects.equal(this.avgTradedVolM3, that.avgTradedVolM3)
					&& Objects.equal(this.avgVolumeM3, that.avgVolumeM3)
					&& Objects.equal(this.basicEpsIncl, that.basicEpsIncl) && Objects.equal(this.beta5Yr, that.beta5Yr)
					&& Objects.equal(this.businessDescription, that.businessDescription)
					&& Objects.equal(this.bvShare, that.bvShare) && Objects.equal(this.companyId, that.companyId)
					&& Objects.equal(this.capitalExpenditures, that.capitalExpenditures)
					&& Objects.equal(this.cashInvestments, that.cashInvestments)
					&& Objects.equal(this.closePrice, that.closePrice)
					&& Objects.equal(this.companyAddress, that.companyAddress)
					&& Objects.equal(this.companyName, that.companyName)
					&& Objects.equal(this.companyWebsite, that.companyWebsite)
					&& Objects.equal(this.divShare, that.divShare) && Objects.equal(this.ebit, that.ebit)
					&& Objects.equal(this.ebitda, that.ebitda) && Objects.equal(this.ebitdaMargin, that.ebitdaMargin)
					&& Objects.equal(this.employees, that.employees) && Objects.equal(this.eps, that.eps)
					&& Objects.equal(this.filingCurrency, that.filingCurrency)
					&& Objects.equal(this.filingDate, that.filingDate)
					&& Objects.equal(this.fiscalYearEnd, that.fiscalYearEnd)
					&& Objects.equal(this.floatPercentage, that.floatPercentage)
					&& Objects.equal(this.gtiScore, that.gtiScore)
					&& Objects.equal(this.gtiRankChange, that.gtiRankChange) && Objects.equal(this.gvKey, that.gvKey)
					&& Objects.equal(this.highPrice, that.highPrice) && Objects.equal(this.industry, that.industry)
					&& Objects.equal(this.industryGroup, that.industryGroup)
					&& Objects.equal(this.lastSalePrice, that.lastSalePrice)
					&& Objects.equal(this.lowPrice, that.lowPrice)
					&& Objects.equal(this.minorityInterest, that.minorityInterest)
					&& Objects.equal(this.netDebt, that.netDebt) && Objects.equal(this.netIncome, that.netIncome)
					&& Objects.equal(this.netProfitMargin, that.netProfitMargin)
					&& Objects.equal(this.openPrice, that.openPrice)
					&& Objects.equal(this.previousCloseDate, that.previousCloseDate)
					&& Objects.equal(this.previousClosePrice, that.previousClosePrice)
					&& Objects.equal(this.priceVolHistYr, that.priceVolHistYr)
					&& Objects.equal(this.returnOnEquity, that.returnOnEquity)
					&& Objects.equal(this.sharesSoldShort, that.sharesSoldShort)
					&& Objects.equal(this.sharesOutstanding, that.sharesOutstanding)
					&& Objects.equal(this.targetPriceNum, that.targetPriceNum)
					&& Objects.equal(this.targetPrice, that.targetPrice)
					&& Objects.equal(this.tickerCode, that.tickerCode) && Objects.equal(this.tbv, that.tbv)
					&& Objects.equal(this.totalAssets, that.totalAssets)
					&& Objects.equal(this.totalDebt, that.totalDebt)
					&& Objects.equal(this.totalDebtEbitda, that.totalDebtEbitda)
					&& Objects.equal(this.totalDebtEquity, that.totalDebtEquity)
					&& Objects.equal(this.totalRev1YrAnnGrowth, that.totalRev1YrAnnGrowth)
					&& Objects.equal(this.totalRev3YrAnnGrowth, that.totalRev3YrAnnGrowth)
					&& Objects.equal(this.totalRev5YrAnnGrowth, that.totalRev5YrAnnGrowth)
					&& Objects.equal(this.totalRevenue, that.totalRevenue)
					&& Objects.equal(this.tradeName, that.tradeName) && Objects.equal(this.volume, that.volume)
					&& Objects.equal(this.vwapAsOfDate, that.vwapAsOfDate)
					&& Objects.equal(this.vwapCurrency, that.vwapCurrency)
					&& Objects.equal(this.volWeightedAvgPrice, that.volWeightedAvgPrice)
					&& Objects.equal(this.yearFounded, that.yearFounded) && Objects.equal(this.yearHigh, that.yearHigh)
					&& Objects.equal(this.yearLow, that.yearLow) && Objects.equal(this.priceHistory, that.priceHistory)
					&& Objects.equal(this.dividendHistory, that.dividendHistory)
					&& Objects.equal(this.marketCap, that.marketCap) 
					&& Objects.equal(this.fullPH, that.fullPH)
					&& Objects.equal(this.volatility, that.volatility)
					&& Objects.equal(this.peRatio, that.peRatio);
		}
		return false;
	}

}
