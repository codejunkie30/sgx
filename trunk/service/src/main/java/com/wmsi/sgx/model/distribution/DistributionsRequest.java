package com.wmsi.sgx.model.distribution;

import java.util.List;

import javax.validation.Valid;
import com.google.common.base.Objects;

public class DistributionsRequest{

	@Valid
	private List<DistributionRequestField> fields;

	public List<DistributionRequestField> getFields() {
		return fields;
	}

	public void setFields(List<DistributionRequestField> fields) {
		this.fields = fields;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(fields);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DistributionsRequest) {
			DistributionsRequest that = (DistributionsRequest) object;
			return Objects.equal(this.fields, that.fields);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("fields", fields)
			.toString();
	}
}
