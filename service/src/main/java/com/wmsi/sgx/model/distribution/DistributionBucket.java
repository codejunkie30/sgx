package com.wmsi.sgx.model.distribution;

import com.google.common.base.Objects;

public class DistributionBucket{

	private Long count;
	private String from;
	private String key;
	private String to;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(count, from, key, to);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DistributionBucket) {
			DistributionBucket that = (DistributionBucket) object;
			return Objects.equal(this.count, that.count)
				&& Objects.equal(this.from, that.from)
				&& Objects.equal(this.key, that.key)
				&& Objects.equal(this.to, that.to);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("count", count)
			.add("from", from)
			.add("key", key)
			.add("to", to)
			.toString();
	}
}