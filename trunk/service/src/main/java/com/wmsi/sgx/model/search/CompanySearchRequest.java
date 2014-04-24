package com.wmsi.sgx.model.search;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import com.google.common.base.Objects;


public class CompanySearchRequest{

	@NotEmpty	
	@Size(min = 1, max = 128, message="Invaid length: Max 128")
	@SafeHtml
	private String search;
	
	public String getSearch() {
		return search;
	}

	public void setSearch(String s) {
		search = s;
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(search);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CompanySearchRequest) {
			CompanySearchRequest that = (CompanySearchRequest) object;
			return Objects.equal(this.search, that.search);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("search", search)
			.toString();
	}
}
