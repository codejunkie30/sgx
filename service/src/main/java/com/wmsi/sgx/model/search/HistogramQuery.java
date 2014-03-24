package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CustomSerializer.class)
public class HistogramQuery extends AbstractQuery{

	private String field;
	private Integer interval;
	private Integer scale = 1;

	public String getField() {
		return field;
	}

	public Integer getInterval() {
		return interval;
	}

	public Integer getScale() {
		return scale;
	}

	public String getScript() {
		return "_value * ".concat(String.valueOf(scale));
	}
	
	public void setField(String field) {
		this.field = field;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}


}
