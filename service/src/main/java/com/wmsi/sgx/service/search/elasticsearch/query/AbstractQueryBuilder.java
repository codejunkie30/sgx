package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.service.search.QueryBuilder;

public abstract class AbstractQueryBuilder<T> implements QueryBuilder<T>{

	@Override
	public String build(T request) {
		return getBuilder(request).toString();
	}
	
	public abstract SearchSourceBuilder getBuilder(T request);
}
