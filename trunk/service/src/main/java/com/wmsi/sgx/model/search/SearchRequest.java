package com.wmsi.sgx.model.search;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;

public class SearchRequest{

	@Valid
	@NotNull
	@Size(min = 1, max = 5, message="Invalid criteria size")
	private List<Criteria> criteria;
	
	public List<Criteria> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criteria> criteria) {
		this.criteria = criteria;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("criteria", criteria)
			.toString();
	}
}
