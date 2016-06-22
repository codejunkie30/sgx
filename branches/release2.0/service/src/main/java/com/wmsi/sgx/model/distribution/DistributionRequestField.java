package com.wmsi.sgx.model.distribution;

import org.hibernate.validator.constraints.NotEmpty;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.validation.annotations.ValidFieldName;

public class DistributionRequestField{

	public DistributionRequestField(){}
	
	public DistributionRequestField(String f){
		field = f;
	}
	
	@NotEmpty
	@ValidFieldName(model = SearchCompany.class)
	private String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(field);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DistributionRequestField) {
			DistributionRequestField that = (DistributionRequestField) object;
			return Objects.equal(this.field, that.field);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("field", field)
			.toString();
	}
}
