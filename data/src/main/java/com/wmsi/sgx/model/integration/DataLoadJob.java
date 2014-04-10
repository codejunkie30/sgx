package com.wmsi.sgx.model.integration;

import java.util.Date;

import org.springframework.core.io.Resource;
import com.google.common.base.Objects;

public class DataLoadJob{

	private Date date;
	private String indexName;
	private Resource tickerFile;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public Resource getTickerFile() {
		return tickerFile;
	}

	public void setTickerFile(Resource tickerFile) {
		this.tickerFile = tickerFile;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(date, indexName, tickerFile);
	}
	
	@Override
	public boolean equals(Object object){
		if (object instanceof DataLoadJob) {
			DataLoadJob that = (DataLoadJob) object;
			return Objects.equal(this.date, that.date)
				&& Objects.equal(this.indexName, that.indexName)
				&& Objects.equal(this.tickerFile, that.tickerFile);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			.add("date", date)
			.add("indexName", indexName)
			.add("tickerFile", tickerFile)
			.toString();
	}
}
