package com.wmsi.sgx.service.search.elasticsearch;

import java.util.List;

import com.google.common.base.Objects;


public class Aggregations{
	
	private List<Aggregation> aggregations;

	public List<Aggregation> getAggregations() {
		return aggregations;
	}

	public void setAggregations(List<Aggregation> aggregations) {
		this.aggregations = aggregations;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("aggregations", aggregations)
			.toString();
	}
}