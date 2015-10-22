package com.wmsi.sgx.model.integration;

import com.google.common.base.Objects;

public class CompanyInputRecord{

	private String id;
	private String ticker;
	private String isin;
	private String date;
	private String tradeName;
	private String legalName;
	private String exSymbol;
	private String currency;
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

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
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
	
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchangeSymbol() {
		return exSymbol;
	}

	public void setExchangeSymbol(String exSymbol) {
		this.exSymbol = exSymbol;
	}
	
	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	
	@Override
	public int hashCode(){
		return Objects.hashCode(id, ticker, isin, date, tradeName, indexed);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof CompanyInputRecord) {
			CompanyInputRecord that = (CompanyInputRecord) object;
			return Objects.equal(this.id, that.id)
				&& Objects.equal(this.ticker, that.ticker)
				&& Objects.equal(this.isin, that.isin)
				&& Objects.equal(this.date, that.date)
				&& Objects.equal(this.tradeName, that.tradeName)
				&& Objects.equal(this.indexed, that.indexed)
				&& Objects.equal(this.currency, that.currency)
				&& Objects.equal(this.exSymbol, that.exSymbol)
				&& Objects.equal(this.legalName, that.legalName);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("id", id)
			.add("ticker", ticker)
			.add("isin", isin)
			.add("date", date)
			.add("tradeName", tradeName)
			.add("indexed", indexed)
			.add("currency", currency)
			.add("exSymbol", exSymbol)
			.add("legalName", legalName)
			.toString();
	}

}
