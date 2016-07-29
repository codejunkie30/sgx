package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class AlphaFactorIdQueryBuilder extends AbstractQueryBuilder{
	
	private String id;
	
	public AlphaFactorIdQueryBuilder(String id){
		this.id = id;
	}
	
	@Override
	public String build() {
		return new SearchSourceBuilder()
		.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.boolFilter()
				.must(FilterBuilders.typeFilter("alphaFactor"))
				.must(FilterBuilders.prefixFilter("companyId", id))))
		.size(MAX_RESULTS)
		.toString();
	}

}
