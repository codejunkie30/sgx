package com.wmsi.sgx.model;

import java.util.Map;
import com.google.common.base.Objects;

public class AlertOption {
	
	public String description;
	public Map<String, String> companies;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, String> getCompanies() {
		return companies;
	}
	public void setCompanies(Map<String, String> companies) {
		this.companies = companies;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(description, companies);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof AlertOption) {
			AlertOption that = (AlertOption) object;
			return Objects.equal(this.description, that.description)
				&& Objects.equal(this.companies, that.companies);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("description", description)
			.add("companies", companies)
			.toString();
	}
	
	

}
