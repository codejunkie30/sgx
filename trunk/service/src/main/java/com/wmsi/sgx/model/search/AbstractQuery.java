package com.wmsi.sgx.model.search;

public abstract class AbstractQuery implements Query{

	private String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
