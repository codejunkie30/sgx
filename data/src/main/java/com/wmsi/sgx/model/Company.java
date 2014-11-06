package com.wmsi.sgx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.ConversionAnnotation;

@JsonRootName("company")
public class Company{
	
	@ConversionAnnotation(name = "IQ_AVG_BROKER_REC_NO_CIQ")
	private Double avgBrokerReq;

	private Double avgVolumeM3;

	@ConversionAnnotation(name = "IQ_BETA_5YR")
	private Double beta5Yr;

	@ConversionAnnotation(name = "IQ_BUSINESS_DESCRIPTION")
	private String businessDescription;

	@ConversionAnnotation(name = "IQ_BV_SHARE")
	private Double bvShare;

	@ConversionAnnotation(name = "IQ_CAPEX")
	private Double capitalExpenditures;

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

	@ConversionAnnotation(name = "IQ_DIVIDEND_YIELD")
	private Double dividendYield;

	@ConversionAnnotation(name = "IQ_EBIT")
	private Double ebit;

	@ConversionAnnotation(name = "IQ_EBITDA")
	private Double ebitda;

	@ConversionAnnotation(name = "IQ_EBITDA_MARGIN")
	private Double ebitdaMargin;

	@ConversionAnnotation(name = "IQ_EMPLOYEES")
	private Double employees;

	@ConversionAnnotation(name = "IQ_TEV")
	private Double enterpriseValue;

	@ConversionAnnotation(name = "IQ_DILUT_EPS_EXCL")
	private Double eps;

	@ConversionAnnotation(name = "IQ_TEV_EBITDA")
	private Double evEbitData;

	@ConversionAnnotation(name = "IQ_FILING_CURRENCY")
	private String filingCurrency;

	@ConversionAnnotation(name = "IQ_PERIODDATE_IS")
	private Date fiscalYearEnd;

	@ConversionAnnotation(name = "IQ_FLOAT_PERCENT")
	private Double floatPercentage;

	@ConversionAnnotation(name = "IQ_GVKEY")
	private String gvKey;

	@ConversionAnnotation(name = "IQ_HIGHPRICE")
	private Double highPrice;

	@ConversionAnnotation(name = "IQ_INDUSTRY")
	private String industry;

	@ConversionAnnotation(name = "IQ_INDUSTRY_GROUP")
	private String industryGroup;

	@ConversionAnnotation(name = "IQ_LOWPRICE")
	private Double lowPrice;

	@ConversionAnnotation(name = "IQ_MARKETCAP")
	private Double marketCap;

	@ConversionAnnotation(name = "IQ_NI")
	private Double netIncome;

	@ConversionAnnotation(name = "IQ_NI_MARGIN")
	private Double netProfitMargin;

	@ConversionAnnotation(name = "IQ_OPENPRICE")
	private Double openPrice;

	@ConversionAnnotation(name = "IQ_PE_EXCL")
	private Double peRatio;

	@ConversionAnnotation(name = "IQ_PRICEDATE")
	private Date previousCloseDate;

	@ConversionAnnotation(name = "IQ_CLOSE_PRICE")
	private Double previousClosePrice;

	@ConversionAnnotation(name = "IQ_PBV")
	private Double priceToBookRatio;
	
	@ConversionAnnotation(name = "IQ_PRICE_VOL_HIST_YR")
	private Double priceVolHistYr;

	@ConversionAnnotation(name = "IQ_SHARESOUTSTANDING")
	private Double sharesOutstanding;

	@ConversionAnnotation(name = "IQ_SHORT_INTEREST")
	private Double sharesSoldShort;

	@ConversionAnnotation(name = "IQ_TARGET_PRICE_NUM_CIQ")
	private Double targetPriceNum;

	@ConversionAnnotation(name = "IQ_PRICE_TARGET_CIQ")
	private Double targetPrice;

	@ConversionAnnotation(name = "IQ_COMPANY_TICKER_NO_EXCH")
	private String tickerCode;

	@ConversionAnnotation(name = "IQ_TOTAL_ASSETS")
	private Double totalAssets;

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

	@ConversionAnnotation(name = "IQ_TOTAL_REV")
	private Double totalRevenue;

	private String tradeName;

	@ConversionAnnotation(name = "IQ_VOLUME")
	private Double volume;

	@ConversionAnnotation(name = "IQ_YEAR_FOUNDED")
	private Integer yearFounded;

	@ConversionAnnotation(name = "IQ_YEARHIGH")
	private Double yearHigh;

	@ConversionAnnotation(name = "IQ_YEARLOW")
	private Double yearLow;

	private List<HistoricalValue> priceHistory;

	public List<HistoricalValue> getPriceHistory() {
		return priceHistory;
	}

	public void setPriceHistory(List<HistoricalValue> priceHistory) {
		this.priceHistory = priceHistory;
	}

	public Double getPriceVs52WeekHigh() {

		if(closePrice == null || closePrice == 0 || yearHigh == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice);
		BigDecimal high = new BigDecimal(yearHigh);
		
		return close.divide(high, RoundingMode.HALF_UP)
				.subtract(BigDecimal.ONE)
				.multiply(new BigDecimal(100))
				.doubleValue();
	}

	public Double getPriceVs52WeekLow() {

		if(closePrice == null || closePrice == 0 || yearLow == null)
			return null;

		BigDecimal close = new BigDecimal(closePrice);
		BigDecimal low = new BigDecimal(yearLow);
		
		return close.divide(low, RoundingMode.HALF_UP)
				.subtract(BigDecimal.ONE)
				.multiply(new BigDecimal(100))
				.doubleValue();
	}

	public Double getAvgBrokerReq() {
		return avgBrokerReq;
	}

	public void setAvgBrokerReq(Double avgBrokerReq) {
		this.avgBrokerReq = avgBrokerReq;
	}

	public Double getAvgVolumeM3() {
		return avgVolumeM3;
	}

	public void setAvgVolumeM3(Double avgVolumeM3) {
		this.avgVolumeM3 = avgVolumeM3;
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
		return priceToBookRatio;
	}

	public void setPriceToBookRatio(Double priceToBookRatio) {
		this.priceToBookRatio = priceToBookRatio;
	}
	
	public Double getPriceVolHistYr(){
		return priceVolHistYr;
	}
	public void setPriceVolHistYr(Double priceVolHistYr){
		this.priceVolHistYr = priceVolHistYr;
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
	

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("avgBrokerReq", avgBrokerReq)
			.add("avgVolumeM3", avgVolumeM3)
			.add("beta5Yr", beta5Yr)
			.add("businessDescription", businessDescription)
			.add("bvShare", bvShare)
			.add("capitalExpenditures", capitalExpenditures)
			.add("cashInvestments", cashInvestments)
			.add("closePrice", closePrice)
			.add("companyAddress", companyAddress)
			.add("companyName", companyName)
			.add("companyWebsite", companyWebsite)
			.add("dividendYield", dividendYield)
			.add("ebit", ebit)
			.add("ebitda", ebitda)
			.add("ebitdaMargin", ebitdaMargin)
			.add("employees", employees)
			.add("enterpriseValue", enterpriseValue)
			.add("eps", eps)
			.add("evEbitData", evEbitData)
			.add("filingCurrency", filingCurrency)
			.add("fiscalYearEnd", fiscalYearEnd)
			.add("floatPercentage", floatPercentage)
			.add("gvKey", gvKey)
			.add("highPrice", highPrice)
			.add("industry", industry)
			.add("industryGroup", industryGroup)
			.add("lowPrice", lowPrice)
			.add("marketCap", marketCap)
			.add("netIncome", netIncome)
			.add("netProfitMargin", netProfitMargin)
			.add("openPrice", openPrice)
			.add("peRatio", peRatio)
			.add("previousCloseDate", previousCloseDate)
			.add("previousClosePrice", previousClosePrice)
			.add("priceToBookRatio", priceToBookRatio)
			.add("sharesOutstanding", sharesOutstanding)
			.add("sharesSoldShort", sharesSoldShort)
			.add("targetPriceNum", targetPriceNum)
			.add("targetPrice", targetPrice)
			.add("tickerCode", tickerCode)
			.add("totalAssets", totalAssets)
			.add("totalDebt", totalDebt)
			.add("totalDebtEbitda", totalDebtEbitda)
			.add("totalDebtEquity", totalDebtEquity)
			.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth)
			.add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
			.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth)
			.add("totalRevenue", totalRevenue)
			.add("tradeName", tradeName)
			.add("volume", volume)
			.add("yearFounded", yearFounded)
			.add("yearHigh", yearHigh)
			.add("yearLow", yearLow)
			.add("priceHistory", priceHistory)
			.add("priceVolHistYr", priceVolHistYr)
			.toString();
	}

}
