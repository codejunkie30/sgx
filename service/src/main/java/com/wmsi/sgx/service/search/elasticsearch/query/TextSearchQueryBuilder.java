package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class TextSearchQueryBuilder extends AbstractQueryBuilder<String>{
	
	private static final int MAX_RESULTS = 2000;
	
	@Override
	public SearchSourceBuilder getBuilder(String text) {
		return new SearchSourceBuilder()
			.query(QueryBuilders.queryString(text)
			.analyzer("sgx_text_ngram_analyzer")
			.field("companyName")
			.field("tickerSearch"))
			.size(MAX_RESULTS);
	}
}
