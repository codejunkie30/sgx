package com.wmsi.sgx.service.search.elasticsearch;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;


public class Aggregation{
	
	private String name;
	
	private List<Bucket> buckets;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Bucket> getBuckets() {
		return buckets;
	}
	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("name", name)
			.add("buckets", buckets)
			.toString();
	}
}

