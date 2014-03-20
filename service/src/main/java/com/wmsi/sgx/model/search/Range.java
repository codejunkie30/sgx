package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class Range{

	public Range(){}
	
	public Range(Double f, Double t){
		from = f;
		to = t;
	}
	
	private Double to;	
	
	@JsonProperty("lte")
	public Double getTo(){return to;}
	
	@JsonProperty("lte")
	public void setTo(Double t){to = t;}
	
	private Double from;
	@JsonProperty("gte")
	public Double getFrom(){return from;}
	
	@JsonProperty("gte")
	public void setFrom(Double f){from = f;}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("from", from)
			.add("to", to)
			.toString();
	}	
}
