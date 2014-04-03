package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.search.input.IdSearch;

public class HistoricalValueQueryBuilder extends AbstractQueryBuilder<IdSearch>{

	@Override
	public SearchSourceBuilder getBuilder(IdSearch id) {
		
		return 	new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.termFilter("tickerCode",  id.getId())))
				.fetchSource(
					new String[]{"date", "value"}, 
					null)
				.size(10000)
				.sort("date");
	}
}