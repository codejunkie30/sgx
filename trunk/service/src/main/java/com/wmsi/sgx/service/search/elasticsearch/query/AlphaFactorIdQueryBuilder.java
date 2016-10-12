package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * 
 * This class is used to build the query related to Alpha factors for elastic
 * search.
 *
 */
public class AlphaFactorIdQueryBuilder extends AbstractQueryBuilder{
	
	private String id;
	
	public AlphaFactorIdQueryBuilder(String id){
		this.id = id;
	}
	
	/**
	 * Returns the query that is built for returning the alpha factors.
	 * 
	 * @return String
	 */
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
