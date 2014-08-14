package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;

public class TickerSearchQueryBuilder extends AbstractQueryBuilder{
	
	private String text;
	
	public TickerSearchQueryBuilder(String text) {
		this.text = text;
	}
		
	@Override
	public String build(){
		
		return new SearchSourceBuilder()
		
			.query(QueryBuilders.boolQuery()

				// Text search for prefix match
				.should(QueryBuilders
						.constantScoreQuery(QueryBuilders
					.matchPhrasePrefixQuery("tickerCode.full", text))
					.boost(4))

				// Search partial matches
				.should(QueryBuilders
						.constantScoreQuery(QueryBuilders
					.matchQuery("tickerCode.partial", text))
					.boost(2)))
					
				.sort("_score")
				.sort("tickerCode")				
				.size(MAX_RESULTS)
				.toString();
	}
}
