package com.wmsi.sgx.model;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.ConversionAnnotation;

public class Holder{

	@ConversionAnnotation(name="IQ_HOLDER_NAME")
	private String name;
	
	@ConversionAnnotation(name="IQ_HOLDER_SHARES")
	private Long shares;
	
	@ConversionAnnotation(name="IQ_HOLDER_PERCENT")
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
	public int hashCode(){
		return Objects.hashCode(name, shares, percent);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Holder) {
			Holder that = (Holder) object;
			return Objects.equal(this.name, that.name)
				&& Objects.equal(this.shares, that.shares)
				&& Objects.equal(this.percent, that.percent);
		}
		return false;
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
