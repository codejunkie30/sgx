package com.wmsi.sgx.service.search.elasticsearch.query;

import com.wmsi.sgx.service.search.QueryBuilder;

public abstract class AbstractQueryBuilder implements QueryBuilder{

	protected static final int MAX_RESULTS = 2000;
	
	@Override
	public abstract String build();
	
}
