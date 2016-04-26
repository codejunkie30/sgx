package com.wmsi.sgx.model;

import java.util.Date;
import com.google.common.base.Objects;

public class PriceCall {
	public String id;
	public Date date;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	@Override
	public int hashCode(){
		return Objects.hashCode(id, date);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof PriceCall) {
			PriceCall that = (PriceCall) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.date, that.date);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("date", date)
			.toString();
	}
	
	
}
