package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;

public class GovTransparencyIndex{

	private Integer adjustment;
	private Integer baseScore;
	private String companyName;
	private Date date;
	private String isin;
	private String issue;
	private Integer rank;
	private Integer rankChange;
	private String ticker;
	private Integer totalScore;

	public Integer getAdjustment() {
		return adjustment;
	}

	public Integer getBaseScore() {
		return baseScore;
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

	public Integer getRankChange() {
		return rankChange;
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

	public void setRankChange(Integer change) {
		this.rankChange = change;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(adjustment, baseScore, rankChange, companyName, date, isin, issue, rank, ticker, totalScore);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof GovTransparencyIndex) {
			GovTransparencyIndex that = (GovTransparencyIndex) object;
			return Objects.equal(this.adjustment, that.adjustment)
				&& Objects.equal(this.baseScore, that.baseScore)
				&& Objects.equal(this.rankChange, that.rankChange)
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
			.add("change", rankChange)
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
