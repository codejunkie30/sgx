package com.wmsi.sgx.service.search.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Bucket{

	private Object key;
	public Object getKey(){return key;}
	public void setKey(Object k){key = k;}
	
	private Long count;
	public Long getCount(){return count;}
	@JsonProperty("doc_count")
	public void setCount(Long c){count = c;}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("key", key)
			.add("count", count)
			.toString();
	}
}
