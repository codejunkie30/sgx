package com.wmsi.sgx.model.distribution;

import com.google.common.base.Objects;

public class DistributionBucket{

	private String key;
	private Long count;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("key", key)
			.add("count", count)
			.toString();
	}
}