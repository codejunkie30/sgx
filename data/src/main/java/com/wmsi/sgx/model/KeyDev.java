package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.conversion.dozer.ConversionAnnotation;

public class KeyDev{

	@ConversionAnnotation(name="IQ_KEY_DEV_DATE")
	private Date date;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_HEADLINE")	
	private String headline;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_SITUATION")	
	private String situation;
	
	public Date getDate() {
		return date;
	}

	public String getHeadline() {
		return headline;
	}

	public String getSituation() {
		return situation;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(date, headline, situation);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof KeyDev) {
			KeyDev that = (KeyDev) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.headline, that.headline)
				&& Objects.equal(this.situation, that.situation);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("headline", headline)
			.add("situation", situation)
			.toString();
	}

	
}
