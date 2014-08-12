package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class TickerSearchQueryBuilder extends AbstractQueryBuilder{
	
	private String text;
	
	public TickerSearchQueryBuilder(String text) {
		this.text = text;
	}
		
	@Override
	public String build(){
		
		return new SearchSourceBuilder()
		
			.query(QueryBuilders.boolQuery()

				// Text search for full phrase match
				.should(QueryBuilders
					.matchPhraseQuery("tickerCode.full", text)
					.boost(5))
					
				// Text search for prefix match
				.should(QueryBuilders
					.matchPhrasePrefixQuery("tickerCode.full", text)
					.boost(4))

				// Search partial matches
				.should(QueryBuilders
					.matchQuery("tickerCode.partial", text)
					.boost(2)))
				
				.size(MAX_RESULTS)
				.toString();
	}
}
