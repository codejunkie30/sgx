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
					.matchPhraseQuery("tickerCode.tickerSearch", text)
					.boost(5))
					
				// Text search for prefix match
				.should(QueryBuilders
					.matchPhrasePrefixQuery("tickerCode.tickerSearch", text)
					.boost(4))
/*
				// Search beginning of text
				.should(QueryBuilders
					.matchQuery("tickerCode.tickerSearch.partial", text)
					.boost(3))

*/				// Search middle of text
				.should(QueryBuilders
					.matchQuery("tickerCode.tickerSearch.partial", text)
					.boost(1))

/*				// Search end of text
				.should(QueryBuilders
					.matchQuery("tickerCode.tickerSearch.partial_back", text)
					.boost(1)))			
*/				).size(MAX_RESULTS)
				.toString();
	}
}
