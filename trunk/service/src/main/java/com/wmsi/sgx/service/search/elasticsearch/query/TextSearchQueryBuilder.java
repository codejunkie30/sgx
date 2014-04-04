package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class TextSearchQueryBuilder extends AbstractQueryBuilder<String>{

	@Override
	public SearchSourceBuilder getBuilder(String text) {
		return new SearchSourceBuilder()
			.query(QueryBuilders.queryString(text)
			.analyzer("snowball")
			.field("companyName")
			.field("tickerSearch"));
	}
}
