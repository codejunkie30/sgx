package com.wmsi.sgx.service.search.elasticsearch;

import java.util.List;

import com.google.common.base.Objects;

public class BucketAggregation extends DefaultAggregation{

	private List<Bucket> buckets;

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> b) {
		buckets = b;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("buckets", buckets)
			.toString();
	}
}
