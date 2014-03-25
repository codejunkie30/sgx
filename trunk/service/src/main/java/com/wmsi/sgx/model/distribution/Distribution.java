package com.wmsi.sgx.model.distribution;

import java.util.List;
import com.google.common.base.Objects;

public class Distribution{

	private String field;
	private List<DistributionBucket> buckets;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<DistributionBucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<DistributionBucket> buckets) {
		this.buckets = buckets;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("field", field)
			.add("buckets", buckets)
			.toString();
	}
}
