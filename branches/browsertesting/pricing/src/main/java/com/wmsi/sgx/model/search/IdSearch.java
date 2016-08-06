package com.wmsi.sgx.model.search;

import com.google.common.base.Objects;

public class IdSearch{

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof IdSearch) {
			IdSearch that = (IdSearch) object;
			return Objects.equal(this.id, that.id);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.toString();
	}	
}
