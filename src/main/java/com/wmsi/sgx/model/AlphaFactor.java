package com.wmsi.sgx.model;

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
	private Integer companyId;

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
	
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(analystExpectations, capitalEfficiency, date, earningsQuality, historicalGrowth, id, priceMomentum, size, valuation, volatility, companyId);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AlphaFactor) {
			AlphaFactor that = (AlphaFactor) object;
			return Objects.equal(this.analystExpectations, that.analystExpectations)
				&& Objects.equal(this.capitalEfficiency, that.capitalEfficiency)
				&& Objects.equal(this.date, that.date)
				&& Objects.equal(this.earningsQuality, that.earningsQuality)
				&& Objects.equal(this.historicalGrowth, that.historicalGrowth)
				&& Objects.equal(this.id, that.id)
				&& Objects.equal(this.priceMomentum, that.priceMomentum)
				&& Objects.equal(this.size, that.size)
				&& Objects.equal(this.valuation, that.valuation)
				&& Objects.equal(this.volatility, that.volatility)
				&& Objects.equal(this.companyId, that.companyId);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("analystExpectations", analystExpectations)
			.add("capitalEfficiency", capitalEfficiency)
			.add("date", date)
			.add("earningsQuality", earningsQuality)
			.add("historicalGrowth", historicalGrowth)
			.add("id", id)
			.add("priceMomentum", priceMomentum)
			.add("size", size)
			.add("valuation", valuation)
			.add("volatility", volatility)
			.add("companyId", companyId)
			.toString();
	}
	
}
