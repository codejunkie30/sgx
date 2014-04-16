package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class FinancialsQueryBuilder extends AbstractQueryBuilder<String>{

	private static final int MAX_RESULTS = 2000;

	@Override
	public SearchSourceBuilder getBuilder(String id) {
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.boolFilter()
						.must(FilterBuilders.termFilter("tickerCode", id))
						.must(FilterBuilders.typeFilter("financial"))))
			.size(MAX_RESULTS)
			.sort("absPeriod", SortOrder.ASC);
	}
}
