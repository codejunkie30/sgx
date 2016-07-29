package com.wmsi.sgx.service.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Bucket{

	private Object key;
	private Double from;
	private Double to;
	
	public Object getKey() {
		return key;
	}

	public void setKey(Object k) {
		key = k;
	}

	public Double getTo() {
		return to;
	}

	public void setTo(Double to) {
		this.to = to;
	}

	public Double getFrom() {
		return from;
	}

	public void setFrom(Double from) {
		this.from = from;
	}

	private Long count;

	public Long getCount() {
		return count;
	}

	@JsonProperty("doc_count")
	public void setCount(Long c) {
		count = c;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(key, from, to, count);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Bucket) {
			Bucket that = (Bucket) object;
			return Objects.equal(this.key, that.key)
				&& Objects.equal(this.from, that.from)
				&& Objects.equal(this.to, that.to)
				&& Objects.equal(this.count, that.count);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("key", key)
			.add("from", from)
			.add("to", to)
			.add("count", count)
			.toString();
	}
}
