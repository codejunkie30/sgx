package com.wmsi.sgx.model;

import java.util.Date;

/**
 * @author dt210908
 *
 */
public class VwapAdjustmentFactor {
	private Date pricingDate;
	private String maxVolume;	
	private String wmsiAPI;
	private String exchangeSymbol;
	private String tickerSymbol;
	
	public Date getPricingDate() {
		return pricingDate;
	}
	public void setPricingDate(Date pricingDate) {
		this.pricingDate = pricingDate;
	}
	public String getMaxVolume() {
		return maxVolume;
	}
	public void setMaxVolume(String maxVolume) {
		this.maxVolume = maxVolume;
	}
	public String getWmsiAPI() {
		return wmsiAPI;
	}
	public void setWmsiAPI(String wmsiAPI) {
		this.wmsiAPI = wmsiAPI;
	}
	public String getExchangeSymbol() {
		return exchangeSymbol;
	}
	public void setExchangeSymbol(String exchangeSymbol) {
		this.exchangeSymbol = exchangeSymbol;
	}
	public String getTickerSymbol() {
		return tickerSymbol;
	}
	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exchangeSymbol == null) ? 0 : exchangeSymbol.hashCode());
		result = prime * result + ((pricingDate == null) ? 0 : pricingDate.hashCode());
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
		VwapAdjustmentFactor other = (VwapAdjustmentFactor) obj;
		if (exchangeSymbol == null) {
			if (other.exchangeSymbol != null)
				return false;
		} else if (!exchangeSymbol.equals(other.exchangeSymbol))
			return false;
		if (pricingDate == null) {
			if (other.pricingDate != null)
				return false;
		} else if (!pricingDate.equals(other.pricingDate))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "VwapAdjustmentFactor [pricingDate=" + pricingDate + ", maxVolume=" + maxVolume + ", wmsiAPI=" + wmsiAPI
				+ ", exchangeSymbol=" + exchangeSymbol + ", tickerSymbol=" + tickerSymbol + "]";
	}	
}
