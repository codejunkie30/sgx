package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class GovTransparencyIndex{

	private Integer adjustment;
	private Integer baseScore;
	private Integer change;
	private String companyName;
	private Date date;
	private String isin;
	private String issue;
	private Integer rank;
	private String ticker;
	private Integer totalScore;

	public Integer getAdjustment() {
		return adjustment;
	}

	public Integer getBaseScore() {
		return baseScore;
	}

	public Integer getChange() {
		return change;
	}

	public String getCompanyName() {
		return companyName;
	}

	public Date getDate() {
		return date;
	}

	public String getIsin() {
		return isin;
	}

	public String getIssue() {
		return issue;
	}

	public Integer getRank() {
		return rank;
	}

	public String getTicker() {
		return ticker;
	}

	public Integer getTotalScore() {
		return totalScore;
	}

	public void setAdjustment(Integer adjustment) {
		this.adjustment = adjustment;
	}

	public void setBaseScore(Integer baseScore) {
		this.baseScore = baseScore;
	}

	public void setChange(Integer change) {
		this.change = change;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(adjustment, baseScore, change, companyName, date, isin, issue, rank, ticker, totalScore);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof GovTransparencyIndex) {
			GovTransparencyIndex that = (GovTransparencyIndex) object;
			return Objects.equal(this.adjustment, that.adjustment)
				&& Objects.equal(this.baseScore, that.baseScore)
				&& Objects.equal(this.change, that.change)
				&& Objects.equal(this.companyName, that.companyName)
				&& Objects.equal(this.date, that.date)
				&& Objects.equal(this.isin, that.isin)
				&& Objects.equal(this.issue, that.issue)
				&& Objects.equal(this.rank, that.rank)
				&& Objects.equal(this.ticker, that.ticker)
				&& Objects.equal(this.totalScore, that.totalScore);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("adjustment", adjustment)
			.add("baseScore", baseScore)
			.add("change", change)
			.add("companyName", companyName)
			.add("date", date)
			.add("isin", isin)
			.add("issue", issue)
			.add("rank", rank)
			.add("ticker", ticker)
			.add("totalScore", totalScore)
			.toString();
	}

}
