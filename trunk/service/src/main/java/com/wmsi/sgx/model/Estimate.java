package com.wmsi.sgx.model;

import java.util.Date;
import java.util.List;
import com.google.common.base.Objects;

public class Estimate {
	
	private Double tpEstimateDeviation;
	private Double tpMeanEstimate;
	private Double tpMedianEstimate;
	private Double tpHighEstimate;
	private Double tpLowEstimate;
	private Double tpEstimateNum;
	private Double targetPrice;
	private Double ltgEstimateDeviation;
	private Double ltgMeanEstimate;
	private Double ltgMedianEstimate;
	private Double ltgHighEstimate;
	private Double ltgLowEstimate;
	private Double ltgEstimateNum;
	private Double volitality;
	private Double industryRec;
	private Double avgBrokerRec;
	private Double normalizedEPS;
	private Double eps;
	private Double revenue;
	private Double ebit;
	private Double ebt;
	private Double netIncomeExcl;
	private Double netIncome;
	private String tickerCode;
	private String period;
	private Date periodDate;
	
	public Date getPeriodDate() {
		return periodDate;
	}
	public void setPeriodDate(Date periodDate) {
		this.periodDate = periodDate;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public Double getTpEstimateDeviation() {
		return tpEstimateDeviation;
	}
	public void setTpEstimateDeviation(Double tpEstimateDeviation) {
		this.tpEstimateDeviation = tpEstimateDeviation;
	}
	public Double getTpMeanEstimate() {
		return tpMeanEstimate;
	}
	public void setTpMeanEstimate(Double tpMeanEstimate) {
		this.tpMeanEstimate = tpMeanEstimate;
	}
	public Double getTpMedianEstimate() {
		return tpMedianEstimate;
	}
	public void setTpMedianEstimate(Double tpMedianEstimate) {
		this.tpMedianEstimate = tpMedianEstimate;
	}
	public Double getTpHighEstimate() {
		return tpHighEstimate;
	}
	public void setTpHighEstimate(Double tpHighEstimate) {
		this.tpHighEstimate = tpHighEstimate;
	}
	public Double gettpLowEstimate() {
		return tpLowEstimate;
	}
	public void settpLowEstimate(Double tpLowEstimate) {
		this.tpLowEstimate = tpLowEstimate;
	}
	public Double getTpEstimateNum() {
		return tpEstimateNum;
	}
	public void setTpEstimateNum(Double tpEstimateNum) {
		this.tpEstimateNum = tpEstimateNum;
	}
	public Double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public Double getLtgEstimateDeviation() {
		return ltgEstimateDeviation;
	}
	public void setLtgEstimateDeviation(Double ltgEstimateDeviation) {
		this.ltgEstimateDeviation = ltgEstimateDeviation;
	}
	public Double getLtgMeanEstimate() {
		return ltgMeanEstimate;
	}
	public void setLtgMeanEstimate(Double ltgMeanEstimate) {
		this.ltgMeanEstimate = ltgMeanEstimate;
	}
	public Double getLtgMedianEstimate() {
		return ltgMedianEstimate;
	}
	public void setLtgMedianEstimate(Double ltgMedianEstimate) {
		this.ltgMedianEstimate = ltgMedianEstimate;
	}
	public Double getLtgHighEstimate() {
		return ltgHighEstimate;
	}
	public void setLtgHighEstimate(Double ltgHighEstimate) {
		this.ltgHighEstimate = ltgHighEstimate;
	}
	public Double getLtgLowEstimate() {
		return ltgLowEstimate;
	}
	public void setLtgLowEstimate(Double ltgLowEstimate) {
		this.ltgLowEstimate = ltgLowEstimate;
	}
	public Double getLtgEstimateNum() {
		return ltgEstimateNum;
	}
	public void setLtgEstimateNum(Double ltgEstimateNum) {
		this.ltgEstimateNum = ltgEstimateNum;
	}
	public Double getVolitality() {
		return volitality;
	}
	public void setVolitality(Double volitality) {
		this.volitality = volitality;
	}
	public Double getIndustryRec() {
		return industryRec;
	}
	public void setIndustryRec(Double industryRec) {
		this.industryRec = industryRec;
	}
	public Double getAvgBrokerRec() {
		return avgBrokerRec;
	}
	public void setAvgBrokerRec(Double avgBrokerRec) {
		this.avgBrokerRec = avgBrokerRec;
	}
	public Double getNormalizedEPS() {
		return normalizedEPS;
	}
	public void setNormalizedEPS(Double normalizedEPS) {
		this.normalizedEPS = normalizedEPS;
	}
	public Double getEps() {
		return eps;
	}
	public void setEps(Double eps) {
		this.eps = eps;
	}
	public Double getRevenue() {
		return revenue;
	}
	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}
	public Double getEbit() {
		return ebit;
	}
	public void setEbit(Double ebit) {
		this.ebit = ebit;
	}
	public Double getEbt() {
		return ebt;
	}
	public void setEbt(Double ebt) {
		this.ebt = ebt;
	}
	public Double getNetIncomeExcl() {
		return netIncomeExcl;
	}
	public void setNetIncomeExcl(Double netIncomeExcl) {
		this.netIncomeExcl = netIncomeExcl;
	}
	public Double getNetIncome() {
		return netIncome;
	}
	public void setNetIncome(Double netIncome) {
		this.netIncome = netIncome;
	}
	public String getTickerCode() {
		return tickerCode;
	}
	public void setTickerCode(String tickerCode) {
		this.tickerCode = tickerCode;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("tpEstimateDeviation", tpEstimateDeviation)
				.add("tpMeanEstimate", tpMeanEstimate).add("tpMedianEstimate", tpMedianEstimate)
				.add("tpHighEstimate", tpHighEstimate).add("tpLowEstimate", tpLowEstimate)
				.add("tpEstimateNum", tpEstimateNum).add("targetPrice", targetPrice)
				.add("ltgEstimateDeviation", ltgEstimateDeviation).add("ltgMeanEstimate", ltgMeanEstimate)
				.add("ltgMedianEstimate", ltgMedianEstimate).add("ltgHighEstimate", ltgHighEstimate)
				.add("ltgLowEstimate", ltgLowEstimate).add("ltgEstimateNum", ltgEstimateNum)
				.add("volitality", volitality).add("industryRec", industryRec).add("avgBrokerRec", avgBrokerRec)
				.add("normalizedEPS", normalizedEPS).add("eps", eps).add("revenue", revenue).add("ebit", ebit)
				.add("ebt", ebt).add("netIncomeExcl", netIncomeExcl).add("netIncome", netIncome)
				.add("tickerCode", tickerCode).add("period", period).add("periodDate", periodDate).toString();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(tpEstimateDeviation, tpMeanEstimate, tpMedianEstimate, tpHighEstimate, tpLowEstimate,
				tpEstimateNum, targetPrice, ltgEstimateDeviation, ltgMeanEstimate, ltgMedianEstimate, ltgHighEstimate,
				ltgLowEstimate, ltgEstimateNum, volitality, industryRec, avgBrokerRec, normalizedEPS, eps, revenue,
				ebit, ebt, netIncomeExcl, netIncome, tickerCode, period, periodDate);
	}
	@Override
	public boolean equals(Object object) {
		if (object instanceof Estimate) {
			Estimate that = (Estimate) object;
			return Objects.equal(this.tpEstimateDeviation, that.tpEstimateDeviation)
					&& Objects.equal(this.tpMeanEstimate, that.tpMeanEstimate)
					&& Objects.equal(this.tpMedianEstimate, that.tpMedianEstimate)
					&& Objects.equal(this.tpHighEstimate, that.tpHighEstimate)
					&& Objects.equal(this.tpLowEstimate, that.tpLowEstimate)
					&& Objects.equal(this.tpEstimateNum, that.tpEstimateNum)
					&& Objects.equal(this.targetPrice, that.targetPrice)
					&& Objects.equal(this.ltgEstimateDeviation, that.ltgEstimateDeviation)
					&& Objects.equal(this.ltgMeanEstimate, that.ltgMeanEstimate)
					&& Objects.equal(this.ltgMedianEstimate, that.ltgMedianEstimate)
					&& Objects.equal(this.ltgHighEstimate, that.ltgHighEstimate)
					&& Objects.equal(this.ltgLowEstimate, that.ltgLowEstimate)
					&& Objects.equal(this.ltgEstimateNum, that.ltgEstimateNum)
					&& Objects.equal(this.volitality, that.volitality)
					&& Objects.equal(this.industryRec, that.industryRec)
					&& Objects.equal(this.avgBrokerRec, that.avgBrokerRec)
					&& Objects.equal(this.normalizedEPS, that.normalizedEPS) && Objects.equal(this.eps, that.eps)
					&& Objects.equal(this.revenue, that.revenue) && Objects.equal(this.ebit, that.ebit)
					&& Objects.equal(this.ebt, that.ebt) && Objects.equal(this.netIncomeExcl, that.netIncomeExcl)
					&& Objects.equal(this.netIncome, that.netIncome) && Objects.equal(this.tickerCode, that.tickerCode)
					&& Objects.equal(this.period, that.period) && Objects.equal(this.periodDate, that.periodDate);
		}
		return false;
	}
	
	
}	