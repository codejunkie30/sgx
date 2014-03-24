package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CustomSerializer.class)
public class StatsQuery extends AbstractQuery{

	private String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
}
