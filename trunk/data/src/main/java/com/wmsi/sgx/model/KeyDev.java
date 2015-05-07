package com.wmsi.sgx.model;

import java.util.Date;

import com.google.common.base.Objects;
import com.wmsi.sgx.model.annotation.ConversionAnnotation;

public class KeyDev{

	@ConversionAnnotation(name="IQ_KEY_DEV_DATE")
	private Date date;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_HEADLINE")	
	private String headline;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_SITUATION")	
	private String situation;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_TYPE")
	private String type;
	
	@ConversionAnnotation(name="IQ_KEY_DEV_SOURCE")
	private String source;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(date, headline, situation, type, source);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof KeyDev) {
			KeyDev that = (KeyDev) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.headline, that.headline)
				&& Objects.equal(this.situation, that.situation)
				&& Objects.equal(this.type, that.type)
				&& Objects.equal(this.source, that.source);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("headline", headline)
			.add("situation", situation)
			.add("type", type)
			.add("source", source)
			.toString();
	}

	
}
