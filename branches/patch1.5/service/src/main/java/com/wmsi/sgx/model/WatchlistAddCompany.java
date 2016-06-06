package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class WatchlistAddCompany {
	public String id;
	public List<String> companies;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getCompanies() {
		return companies;
	}
	public void setCompanies(List<String> companies) {
		this.companies = companies;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(id, companies);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WatchlistAddCompany) {
			WatchlistAddCompany that = (WatchlistAddCompany) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.companies, that.companies);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("companies", companies)
			.toString();
	}
}
