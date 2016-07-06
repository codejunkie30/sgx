package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class CapIQRow{

	@JsonProperty("Row")
	private List<String> values;

	public List<String> getValues(){return values;}
	public void setValues(List<String> v){values = v;}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(values);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQRow) {
			CapIQRow that = (CapIQRow) object;
			return Objects.equal(this.values, that.values);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("values", values)
			.toString();
	}
}
