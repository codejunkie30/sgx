package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;

public class FinancialsQueryBuilder extends AbstractQueryBuilder{

	private String id;

	public FinancialsQueryBuilder(String id) {
		this.id = id;
	}
	
	@Override
	public String build(){
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.boolFilter()
						.must(FilterBuilders.termFilter("tickerCode", id))
						.must(FilterBuilders.typeFilter("financial"))))
			.size(MAX_RESULTS)
			.sort(SortBuilders.fieldSort("absPeriod").ignoreUnmapped(true))
			.toString();			
	}
}
