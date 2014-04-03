package com.wmsi.sgx.service.search;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Search<T> {

	private static final Logger log = LoggerFactory.getLogger(Search.class);

	private File query;
	private String indexName;
	private String type;
	private Class<T> resultClass;
	private QueryBuilder<T> queryBuilder;

	public QueryBuilder<T> getQueryBuilder() {
		return queryBuilder;
	}

	public void setQueryBuilder(QueryBuilder<T> queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public String buildQuery(T criteria){
		return queryBuilder.build(criteria);
	}
	
	public String getQuery() {
		String q = null;
		try{
			q = FileUtils.readFileToString(query);
		}
		catch(IOException e){
			log.error("Could not load query from file ", e);
		}

		return q;
	}

	public void setQuery(File query) {
		this.query = query;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Class<T> getResultClass() {
		return resultClass;
	}

	public void setResultClass(Class<T> class1) {
		this.resultClass = class1;
	}

}
