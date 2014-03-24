package com.wmsi.sgx.service.search.elasticsearch;

import com.google.common.base.Objects;

public class DefaultAggregation extends Aggregation{

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("super", super.toString()).add("value", value).toString();
	}

}
