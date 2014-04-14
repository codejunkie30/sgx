package com.wmsi.sgx.model.sandp;

import java.util.Date;
import com.google.common.base.Objects;

public class AlphaFactor{

	private Integer analystExpectations;
	private Integer capitalEfficiency;
	private Date date;
	private Integer earningsQuality;
	private Integer historicalGrowth;
	private String id;
	private Integer priceMomentum;
	private Integer size;
	private Integer valuation;
	private Integer volatility;

	public Integer getAnalystExpectations() {
		return analystExpectations;
	}

	public Integer getCapitalEfficiency() {
		return capitalEfficiency;
	}

	public Date getDate() {
		return date;
	}

	public Integer getEarningsQuality() {
		return earningsQuality;
	}

	public Integer getHistoricalGrowth() {
		return historicalGrowth;
	}

	public String getId() {
		return id;
	}

	public Integer getPriceMomentum() {
		return priceMomentum;
	}

	public Integer getSize() {
		return size;
	}

	public Integer getValuation() {
		return valuation;
	}

	public Integer getVolatility() {
		return volatility;
	}

	public void setAnalystExpectations(Integer analystExpectations) {
		this.analystExpectations = analystExpectations;
	}

	public void setCapitalEfficiency(Integer capitalEfficiency) {
		this.capitalEfficiency = capitalEfficiency;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setEarningsQuality(Integer earningsQuality) {
		this.earningsQuality = earningsQuality;
	}

	public void setHistoricalGrowth(Integer historicalGrowth) {
		this.historicalGrowth = historicalGrowth;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPriceMomentum(Integer priceMomentum) {
		this.priceMomentum = priceMomentum;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setValuation(Integer valuation) {
		this.valuation = valuation;
	}

	public void setVolatility(Integer volatility) {
		this.volatility = volatility;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("analystExpectations", analystExpectations)
				.add("capitalEfficiency", capitalEfficiency).add("date", date).add("earningsQuality", earningsQuality)
				.add("historicalGrowth", historicalGrowth).add("id", id).add("priceMomentum", priceMomentum)
				.add("size", size).add("valuation", valuation).add("volatility", volatility).toString();
	}
}
