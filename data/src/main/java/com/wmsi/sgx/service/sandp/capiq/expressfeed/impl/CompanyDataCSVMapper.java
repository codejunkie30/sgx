package com.wmsi.sgx.service.sandp.capiq.expressfeed.impl;

import java.util.Date;

public class CompanyDataCSVMapper {
	
	private String tickerSymbol;
	private String exchangeSymbol;
	private String wmsiApi;
	private String wmsiApi_Value;
	private Date period;
	private String currency;
	
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}
	public String getExchangeSymbol() {
		return exchangeSymbol;
	}
	public void setExchangeSymbol(String exchangeSymbol) {
		this.exchangeSymbol = exchangeSymbol;
	}
	public String getWmsiApi() {
		return wmsiApi;
	}
	public void setWmsiApi(String wmsiApi) {
		this.wmsiApi = wmsiApi;
	}
	public String getWmsiApi_Value() {
		return wmsiApi_Value;
	}
	public void setWmsiApi_Value(String wmsiApi_Value) {
		this.wmsiApi_Value = wmsiApi_Value;
	}
	public Date getPeriod() {
		return period;
	}
	public void setPeriod(Date period) {
		this.period = period;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Override
	public String toString() {
		return "CompanyDataCSVMapper [tickerSymbol=" + tickerSymbol + ", exchangeSymbol=" + exchangeSymbol
				+ ", wmsiApi=" + wmsiApi + ", wmsiApi_Value=" + wmsiApi_Value + ", period=" + period + ", currency="
				+ currency + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((exchangeSymbol == null) ? 0 : exchangeSymbol.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((tickerSymbol == null) ? 0 : tickerSymbol.hashCode());
		result = prime * result + ((wmsiApi == null) ? 0 : wmsiApi.hashCode());
		result = prime * result + ((wmsiApi_Value == null) ? 0 : wmsiApi_Value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompanyDataCSVMapper other = (CompanyDataCSVMapper) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (exchangeSymbol == null) {
			if (other.exchangeSymbol != null)
				return false;
		} else if (!exchangeSymbol.equals(other.exchangeSymbol))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (tickerSymbol == null) {
			if (other.tickerSymbol != null)
				return false;
		} else if (!tickerSymbol.equals(other.tickerSymbol))
			return false;
		if (wmsiApi == null) {
			if (other.wmsiApi != null)
				return false;
		} else if (!wmsiApi.equals(other.wmsiApi))
			return false;
		if (wmsiApi_Value == null) {
			if (other.wmsiApi_Value != null)
				return false;
		} else if (!wmsiApi_Value.equals(other.wmsiApi_Value))
			return false;
		return true;
	}

	
}
