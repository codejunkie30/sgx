package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class AlphaFactorIdQueryBuilder extends AbstractQueryBuilder<String>{
	
	private static final int MAX_RESULTS = 2000;
	
	@Override
	public SearchSourceBuilder getBuilder(String id) {
		return new SearchSourceBuilder()
		.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.boolFilter()
				.must(FilterBuilders.typeFilter("alphaFactor"))
				.must(FilterBuilders.prefixFilter("id", id))))
				.size(MAX_RESULTS);
	}

}
