package com.wmsi.sgx.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

public class FieldValue{

	private String field;
	private List<Double> values;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}

	public void addValue(Double o) {
		if(values == null)
			values = new ArrayList<Double>();

		values.add(o);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(field, values);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof FieldValue) {
			FieldValue that = (FieldValue) object;
			return Objects.equal(this.field, that.field)
				&& Objects.equal(this.values, that.values);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("field", field)
			.add("values", values)
			.toString();
	}
	

}
