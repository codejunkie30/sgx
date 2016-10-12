package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;

/**
 * 
 * This class is used to build QUery Builder for estimates.
 *
 */
public class EstimatesQueryBuilder extends AbstractQueryBuilder{
	
	private String id;
	
	public EstimatesQueryBuilder(String id){
		this.id = id;
	}
	
	/**
	 * Builds the estimates query.
	 * 
	 * @return String
	 */
	@Override
	public String build() {
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.boolFilter()
						.must(FilterBuilders.termFilter("tickerCode", id))
						.must(FilterBuilders.typeFilter("estimate"))))
			.size(MAX_RESULTS)
			.sort(SortBuilders.fieldSort("period").ignoreUnmapped(true))
			.toString();
	}

}
