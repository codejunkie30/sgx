package com.wmsi.sgx.model.sandp.capiq;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

public class CapIQResult{

	@JsonProperty("Headers")
	private List<String> headers;
	public List<String> getHeaders(){return headers;}
	public void setHeaders(List<String> h){headers = h;}
	
	@JsonProperty("Rows")
	private List<CapIQRow> rows;
	public List<CapIQRow> getRows(){return rows;}
	public void setRows(List<CapIQRow> r){rows = r;}
	
	@JsonProperty("Mnemonic")
	private String mnemonic;
	public String getMnemonic(){return mnemonic;}
	public void setMnemonic(String m){mnemonic = m;}
	
	@JsonProperty("Properties")
	private CapIQProperty properties;	
	public CapIQProperty getProperties(){return properties;}
	public void setProperties(CapIQProperty p){properties = p;}

	@JsonProperty(value="ErrMsg")
	private String errorMsg;
	public String getErrorMsg(){return errorMsg;}
	public void setErrorMsg(String e){errorMsg = e;}
	
	@JsonProperty(value="Identifier")					     
	private String identifier;
	public String getIdentifier(){return identifier;}
	public void setIdentifier(String i){identifier = i;}

	@JsonProperty(value="NumCols")
	private String numCols;
	public String getNumCols(){return numCols;}
	public void setNumCols(String n){numCols = n;}

	@JsonProperty(value="NumRows")
	private String numRows;
	public String getNumRows(){return numRows;}
	public void setNumRows(String n){numRows = n;}

	@JsonProperty(value="Function")
	private String function;
	public String getFunction(){return function;}
	public void setFunction(String f){function = f;}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(headers, rows, mnemonic, properties, errorMsg, identifier, numCols, numRows, function);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CapIQResult) {
			CapIQResult that = (CapIQResult) object;
			return Objects.equal(this.headers, that.headers)
				&& Objects.equal(this.rows, that.rows)
				&& Objects.equal(this.mnemonic, that.mnemonic)
				&& Objects.equal(this.properties, that.properties)
				&& Objects.equal(this.errorMsg, that.errorMsg)
				&& Objects.equal(this.identifier, that.identifier)
				&& Objects.equal(this.numCols, that.numCols)
				&& Objects.equal(this.numRows, that.numRows)
				&& Objects.equal(this.function, that.function);
		}
		return false;
	}
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("headers", headers)
			.add("rows", rows)
			.add("mnemonic", mnemonic)
			.add("properties", properties)
			.add("errorMsg", errorMsg)
			.add("identifier", identifier)
			.add("numCols", numCols)
			.add("numRows", numRows)
			.add("function", function)
			.toString();
	}
	
	
}
