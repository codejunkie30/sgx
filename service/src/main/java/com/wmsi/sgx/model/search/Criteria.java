package com.wmsi.sgx.model.search;

import com.google.common.base.Objects;

public class Criteria{

	private String field;
	private String value;
	private Double to;
	private Double from;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("field", field)
			.add("value", value)
			.add("to", to)
			.add("from", from)
			.toString();
	}
}
