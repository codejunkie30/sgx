package com.wmsi.sgx.model.distribution;

import java.util.List;

import javax.validation.Valid;

public class DistributionsRequest{

	@Valid
	private List<DistributionRequestField> fields;

	public List<DistributionRequestField> getFields() {
		return fields;
	}

	public void setFields(List<DistributionRequestField> fields) {
		this.fields = fields;
	}
}
