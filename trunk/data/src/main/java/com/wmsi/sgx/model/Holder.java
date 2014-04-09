package com.wmsi.sgx.model;

import com.google.common.base.Objects;

public class Holder{
	private String name;
	private Long shares;
	private Double percent;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getShares() {
		return shares;
	}
	public void setShares(Long shares) {
		this.shares = shares;
	}
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("name", name)
			.add("shares", shares)
			.add("percent", percent)
			.toString();
	}
	
}
