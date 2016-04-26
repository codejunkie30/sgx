package com.wmsi.sgx.model.search;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.validation.annotations.CriteriaSize;

public class SearchRequest{

	@Valid
	@NotNull
	@CriteriaSize(min = 0, max = 5, ignoreFields = {"industry", "targetPriceNum", "exchange" })
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

	@Override
	public int hashCode(){
		return Objects.hashCode(criteria);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof SearchRequest) {
			SearchRequest that = (SearchRequest) object;
			return Objects.equal(this.criteria, that.criteria);
		}
		return false;
	}
}
