package com.wmsi.sgx.model.search;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.validation.annotations.ValidFieldName;

public class Criteria{

	@NotEmpty
	@ValidFieldName(model = SearchCompany.class)
	private String field;
	
	private String value;
	
	private Object to;
	
	private Object from;

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

	public Object getTo() {
		return to;
	}

	public void setTo(Object to) {
		this.to = to;
	}

	public Object getFrom() {
		return from;
	}

	public void setFrom(Object from) {
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

	@Override
	public int hashCode(){
		return Objects.hashCode(field, value, to, from);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof Criteria) {
			Criteria that = (Criteria) object;
			return Objects.equal(this.field, that.field)
				&& Objects.equal(this.value, that.value)
				&& Objects.equal(this.to, that.to)
				&& Objects.equal(this.from, that.from);
		}
		return false;
	}
}
