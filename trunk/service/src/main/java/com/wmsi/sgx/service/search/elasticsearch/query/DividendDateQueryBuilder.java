package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class DividendDateQueryBuilder extends AbstractQueryBuilder{
	
	private static final int MAX_RESULTS = 10000;
	
	private String id;
	
	public DividendDateQueryBuilder (String id) {
		this.id = id;
	}
	
	@Override
	public String build(){
		return	new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.termFilter("tickerCode",  id)))
				.fetchSource(
					new String[]{"dateValue", "asOfDate"}, 
					null)
			.size(MAX_RESULTS)
			.sort("asOfDate")
			.toString();
	}
}