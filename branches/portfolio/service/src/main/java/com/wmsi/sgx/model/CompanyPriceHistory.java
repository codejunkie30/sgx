/**
 * 
 */
package com.wmsi.sgx.model;

import java.util.List;

import com.google.common.base.Objects;

/**
 * @author dt84327
 *
 */
public class CompanyPriceHistory {

	private List<HistoricalValue> price;
	
	
	public List<HistoricalValue> getPrice() {
		return price;
	}

	public void setPrice(List<HistoricalValue> price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("price", price)
			.toString();
	}

	@Override
	public boolean equals(Object object){
		if (object instanceof CompanyPriceHistory) {
			CompanyPriceHistory that = (CompanyPriceHistory) object;
			return Objects.equal(this.price, that.price);
		}
		return false;
	}
}
