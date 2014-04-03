package com.wmsi.sgx.model.alpha;

import com.google.common.base.Objects;

public class AlphaFactorSearchRequest{

	private Integer analystExpectations;
	private Integer capitalEfficiency;
	private Integer earningsQuality;
	private Integer historicalGrowth;
	private Integer priceMomentum;
	private Integer size;
	private Integer valuation;
	private Integer volatility;

	public Integer getAnalystExpectations() {
		return analystExpectations;
	}

	public void setAnalystExpectations(Integer analystExpectations) {
		this.analystExpectations = analystExpectations;
	}

	public Integer getCapitalEfficiency() {
		return capitalEfficiency;
	}

	public void setCapitalEfficiency(Integer capitalEfficiency) {
		this.capitalEfficiency = capitalEfficiency;
	}

	public Integer getEarningsQuality() {
		return earningsQuality;
	}

	public void setEarningsQuality(Integer earningsQuality) {
		this.earningsQuality = earningsQuality;
	}

	public Integer getHistoricalGrowth() {
		return historicalGrowth;
	}

	public void setHistoricalGrowth(Integer historicalGrowth) {
		this.historicalGrowth = historicalGrowth;
	}

	public Integer getPriceMomentum() {
		return priceMomentum;
	}

	public void setPriceMomentum(Integer priceMomentum) {
		this.priceMomentum = priceMomentum;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getValuation() {
		return valuation;
	}

	public void setValuation(Integer valuation) {
		this.valuation = valuation;
	}

	public Integer getVolatility() {
		return volatility;
	}

	public void setVolatility(Integer volatility) {
		this.volatility = volatility;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("analystExpectations", analystExpectations)
				.add("capitalEfficiency", capitalEfficiency).add("earningsQuality", earningsQuality)
				.add("historicalGrowth", historicalGrowth).add("priceMomentum", priceMomentum).add("size", size)
				.add("valuation", valuation).add("volatility", volatility).toString();
	}
}
