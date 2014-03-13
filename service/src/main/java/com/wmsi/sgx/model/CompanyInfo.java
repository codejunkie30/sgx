package com.wmsi.sgx.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Objects;

@JsonRootName("company")
public class CompanyInfo{

	private Double avgBrokerReq;

	private Double avgVolumeM3= 0.0D; // TODO
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
	private Date fiscalYearEnd;
	private Double floatPercentage;
	private String gvKey;
	private Double highPrice;
	private List<Holder> holders;
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
	private Double sharesOutstanding;
	private Double sharesSoldShort;
	private Double targetPriceNum;
	private Double tevData;
	private String tickerCode;
	private Double totalAssets;
	private Double totalDebtEbitda;
	private Double totalDebtEquity;
	private Double totalRev1YrAnnGrowth;
	private Double totalRev3YrAnnGrowth;
	private Double totalRev5YrAnnGrowth;
	private Double totalRevenue;
	private Double volume;
	private Integer yearFounded;
	private Double yearHigh;
	private Double yearLow;
	public Double getAvgBrokerReq(){
		return avgBrokerReq;
	}
	public Double getBeta5Yr() {
		return beta5Yr;
	}	
    public String getBusinessDescription() {
		return businessDescription;
	}
    public Double getBvShare() {
		return bvShare;
	}
    public Double getCapitalExpenditures() {
		return capitalExpenditures;
	}
    
	public Double getCashInvestments() {
		return cashInvestments;
	}
	public Double getClosePrice() {
		return closePrice;
	}
	public String getCompanyAddress() {
		return companyAddress;
	}
	public String getCompanyName() {
		return companyName;
	}
	public String getCompanyWebsite() {
		return companyWebsite;
	}
	public Double getDividendYield() {
		return dividendYield;
	}
	public Double getEbit() {
		return ebit;
	}
	public Double getEbitda() {
		return ebitda;
	}
	public Double getEbitdaMargin() {
		return ebitdaMargin;
	}
	public Double getEmployees() {
		return employees;
	}
    public Double getEnterpriseValue() {
		return enterpriseValue;
	}
    public Double getEps() {
		return eps;
	}    
	public Date getFiscalYearEnd() {
		return fiscalYearEnd;
	}
	public Double getFloatPercentage() {
		return floatPercentage;
	}
	public String getGvKey() {
		return gvKey;
	}
	public Double getHighPrice() {
		return highPrice;
	}
	public List<Holder> getHolders() {
		return holders;
	}
	public String getIndustry() {
		return industry;
	}
	public String getIndustryGroup() {
		return industryGroup;
	}
	public Double getLowPrice() {
		return lowPrice;
	}
	public Double getMarketCap() {
		return marketCap;
	}
	public Double getNetIncome() {
		return netIncome;
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
	public Date getPreviousCloseDate() {
		return previousCloseDate;
	}
	public Double getPreviousClosePrice() {
		return previousClosePrice;
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
	public Double getSharesOutstanding() {
		return sharesOutstanding;
	}
	public Double getSharesSoldShort() {
		return sharesSoldShort;
	}
	public Double getTargetPriceNum() {
		return targetPriceNum;
	}
	
	public Double getTevData() {
		return tevData;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public Double getTotalAssets() {
		return totalAssets;
	}
	public Double getTotalDebtEbitda() {
		return totalDebtEbitda;
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
	public Integer getYearFounded() {
		return yearFounded;
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
	public void setAvgVolumeM3(Double avgVolumeM3) {
		this.avgVolumeM3 = avgVolumeM3;
	}
	public void setBeta5Yr(Double beta5Yr) {
		this.beta5Yr = beta5Yr;
	}
	public void setBusinessDescription(String businessDescription) {
		this.businessDescription = businessDescription;
	}
	public void setBvShare(Double bvShare) {
		this.bvShare = bvShare;
	}
	public void setCapitalExpenditures(Double capitalExpenditures) {
		this.capitalExpenditures = capitalExpenditures;
	}
	public void setCashInvestments(Double cashInvestments) {
		this.cashInvestments = cashInvestments;
	}
	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	public void setCompanyName(String name) {
		this.companyName = name;
	}
	public void setCompanyWebsite(String companyWebsite) {
		this.companyWebsite = companyWebsite;
	}
	public void setDividendYield(Double dividendYield) {
		this.dividendYield = dividendYield;
	}
	public void setEbit(Double ebit) {
		this.ebit = ebit;
	}
	public void setEbitda(Double ebitda) {
		this.ebitda = ebitda;
	}
	public void setEbitdaMargin(Double ebitdaMargin) {
		this.ebitdaMargin = ebitdaMargin;
	}
	public void setEmployees(Double employees) {
		this.employees = employees;
	}
	public void setEnterpriseValue(Double enterpriseValue) {
		this.enterpriseValue = enterpriseValue;
	}
	
	public void setEps(Double eps) {
		this.eps = eps;
	}
	public void setFiscalYearEnd(Date fiscalYearEnd) {
		this.fiscalYearEnd = fiscalYearEnd;
	}
	public void setFloatPercentage(Double floatPercentage) {
		this.floatPercentage = floatPercentage;
	}
	public void setGvKey(String gvKey) {
		this.gvKey = gvKey;
	}
	
	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}
	public void setHolders(List<Holder> holders) {
		this.holders = holders;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public void setIndustryGroup(String industryGroup) {
		this.industryGroup = industryGroup;
	}
	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}
	public void setMarketCap(Double marketCap) {
		this.marketCap = marketCap;
	}
	public void setNetIncome(Double netIncome) {
		this.netIncome = netIncome;
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
	public void setPreviousCloseDate(Date previousCloseDate) {
		this.previousCloseDate = previousCloseDate;
	}
	public void setPreviousClosePrice(Double previousClosePrice) {
		this.previousClosePrice = previousClosePrice;
	}
	
	public void setSharesOutstanding(Double sharesOutstanding) {
		this.sharesOutstanding = sharesOutstanding;
	}
	public void setSharesSoldShort(Double sharesSoldShort) {
		this.sharesSoldShort = sharesSoldShort;
	}
	public void setTargetPriceNum(Double targetPriceNum) {
		this.targetPriceNum = targetPriceNum;
	}
	public void setTevData(Double tevData) {
		this.tevData = tevData;
	}
	public void setTickerCode(String id) {
		this.tickerCode = id;
	}
	public void setTotalAssets(Double totalAssets) {
		this.totalAssets = totalAssets;
	}
	public void setTotalDebtEbitda(Double totalDebtEbitda) {
		this.totalDebtEbitda = totalDebtEbitda;
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
	
	public void setYearFounded(Integer yearFounded) {
		this.yearFounded = yearFounded;
	}
	
	public void setYearHigh(Double yearHigh) {
		this.yearHigh = yearHigh;
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
			.add("fiscalYearEnd", fiscalYearEnd)
			.add("floatPercentage", floatPercentage)
			.add("gvKey", gvKey)
			.add("highPrice", highPrice)
			.add("holders", holders)
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
			.add("sharesOutstanding", sharesOutstanding)
			.add("sharesSoldShort", sharesSoldShort)
			.add("targetPriceNum", targetPriceNum)
			.add("tevData", tevData)
			.add("tickerCode", tickerCode)
			.add("totalAssets", totalAssets)
			.add("totalDebtEbitda", totalDebtEbitda)
			.add("totalDebtEquity", totalDebtEquity)
			.add("totalRev1YrAnnGrowth", totalRev1YrAnnGrowth)
			.add("totalRev3YrAnnGrowth", totalRev3YrAnnGrowth)
			.add("totalRev5YrAnnGrowth", totalRev5YrAnnGrowth)
			.add("totalRevenue", totalRevenue)
			.add("volume", volume)
			.add("yearFounded", yearFounded)
			.add("yearHigh", yearHigh)
			.add("yearLow", yearLow)
			.toString();
	}
}
