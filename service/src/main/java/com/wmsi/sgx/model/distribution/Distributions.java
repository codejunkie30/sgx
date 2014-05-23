package com.wmsi.sgx.model.distribution;

import java.util.List;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.FieldValue;

public class Distributions{

	private List<Distribution> distributions;
	private List<FieldValue> fieldValues;
	
	public List<FieldValue> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(List<FieldValue> fieldValues) {
		this.fieldValues = fieldValues;
	}

	public List<Distribution> getDistributions() {
		return distributions;
	}


	public void setDistributions(List<Distribution> distributions) {
		this.distributions = distributions;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(distributions, fieldValues);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Distributions) {
			Distributions that = (Distributions) object;
			return Objects.equal(this.distributions, that.distributions)
				&& Objects.equal(this.fieldValues, that.fieldValues);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("distributions", distributions)
			.add("fieldValues", fieldValues)
			.toString();
	}
	

}
