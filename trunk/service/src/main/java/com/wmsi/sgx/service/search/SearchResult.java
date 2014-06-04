package com.wmsi.sgx.service.search;

import java.util.List;

import com.google.common.base.Objects;

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

	@Override
	public int hashCode(){
		return Objects.hashCode(hits, aggregations);
	}
		
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object object){
		if (object instanceof SearchResult) {			
			SearchResult that = (SearchResult) object;
			return Objects.equal(this.hits, that.hits)
				&& Objects.equal(this.aggregations, that.aggregations);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("hits", hits)
			.add("aggregations", aggregations)
			.toString();
	}
	
	
}
