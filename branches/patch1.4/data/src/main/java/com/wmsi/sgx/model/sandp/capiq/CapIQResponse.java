package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class CapIQResponse{
	
	@JsonProperty(value="GDSSDKResponse")
	private List<CapIQResult> results;
	
	public List<CapIQResult> getResults(){return results;}
	public void setResults(List<CapIQResult> r){results = r;}
	
	@JsonProperty(value="Errors")
	private String errorMsg;
	
	public String getErrorMsg(){return errorMsg;}
	public void setErrorMsg(String e){errorMsg = e;}

	@Override
	public int hashCode(){
		return Objects.hashCode(results, errorMsg);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQResponse) {
			CapIQResponse that = (CapIQResponse) object;
			return Objects.equal(this.results, that.results)
				&& Objects.equal(this.errorMsg, that.errorMsg);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("results", results)
			.add("errorMsg", errorMsg)
			.toString();
	}

}
