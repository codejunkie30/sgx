package com.wmsi.sgx.model.integration;

import com.google.common.base.Objects;

public class CompanyInputRecord{

	private String id;
	private String ticker;
	private String date;
	private String tradeName;

	private Boolean indexed = false; 
	
	public Boolean getIndexed() {
		return indexed;
	}

	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id, ticker, date, tradeName, indexed);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CompanyInputRecord) {
			CompanyInputRecord that = (CompanyInputRecord) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.ticker, that.ticker)
				&& Objects.equal(this.date, that.date)
				&& Objects.equal(this.tradeName, that.tradeName)
				&& Objects.equal(this.indexed, that.indexed);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("ticker", ticker)
			.add("date", date)
			.add("tradeName", tradeName)
			.add("indexed", indexed)
			.toString();
	}
}
