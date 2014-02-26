package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CapIQRow{

	@JsonProperty("Row")
	private List<String> values;

	public List<String> getValues(){return values;}
	public void setValues(List<String> v){values = v;}
}
