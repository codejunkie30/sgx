package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class HistoricalValueQueryBuilder extends AbstractQueryBuilder<String>{
	
	private static final int MAX_RESULTS = 10000;
	
	@Override
	public SearchSourceBuilder getBuilder(String id) {
		
		return 	new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.termFilter("tickerCode",  id)))
				.fetchSource(
					new String[]{"date", "value"}, 
					null)
				.size(MAX_RESULTS)
				.sort("date");
	}
}