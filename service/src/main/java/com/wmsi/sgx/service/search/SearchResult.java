package com.wmsi.sgx.service.search;

import java.util.List;

public class SearchResult<T> {

	private List<T> hits;
	private Aggregations aggregations;

	public List<T> getHits() {
		return hits;
	}

	public void setHits(List<T> hits) {
		this.hits = hits;
	}

	public Aggregations getAggregations() {
		return aggregations;
	}

	public void setAggregations(Aggregations aggregations) {
		this.aggregations = aggregations;
	}
}
