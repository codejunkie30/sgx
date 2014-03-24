package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;

@JsonSerialize(using = CustomSerializer.class)
public class RangeQuery extends AbstractQuery{

	private String field;
	private Range range;
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public Range getRange() {
		return range;
	}
	public void setRange(Range range) {
		this.range = range;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("field", field)
			.add("range", range)
			.toString();
	}

}
