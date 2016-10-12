package com.wmsi.sgx.service.search.elasticsearch.query;

import com.wmsi.sgx.service.search.QueryBuilder;

/**
 * 
 * This abstract class declares the method to build the query for elastic
 * search.
 *
 */
public abstract class AbstractQueryBuilder implements QueryBuilder{

	protected static final int MAX_RESULTS = 2000;
	
	@Override
	public abstract String build();
	
}
