package com.wmsi.sgx.model;

import java.util.List;
import java.util.Map;
import com.google.common.base.Objects;

public class WatchlistModel {
	public String id;
	public String name;
	public List<String> companies;
	public Map<String, Object> optionList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getCompanies() {
		return companies;
	}
	public void setCompanies(List<String> companies) {
		this.companies = companies;
	}
	public Map<String, Object> getOptionList() {
		return optionList;
	}
	public void setOptionList(Map<String, Object> optionList) {
		this.optionList = optionList;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(id, name, companies, optionList);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof WatchlistModel) {
			WatchlistModel that = (WatchlistModel) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.name, that.name)
				&& Objects.equal(this.companies, that.companies)
				&& Objects.equal(this.optionList, that.optionList);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("name", name)
			.add("companies", companies)
			.add("optionList", optionList)
			.toString();
	}
	
	
}
