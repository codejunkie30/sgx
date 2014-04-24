package com.wmsi.sgx.service.search;

import com.google.common.base.Objects;

public class Aggregation{

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", name).toString();
	}
}
