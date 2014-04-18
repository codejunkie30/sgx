package com.wmsi.sgx.model.search;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Objects;

public class SearchRequest{

	@Valid
	@NotNull
	@Size(min = 0, max = 6, message="Invalid criteria size")
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
