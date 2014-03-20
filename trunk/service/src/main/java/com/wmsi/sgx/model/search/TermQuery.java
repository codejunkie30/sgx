package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;

@JsonSerialize(using = CustomSerializer.class)
public class TermQuery extends AbstractQuery{

	private String field;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("super", super.toString())
			.add("field", field)
			.add("value", value)
			.toString();
	}
	
}
