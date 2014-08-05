package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class TextSearchQueryBuilder extends AbstractQueryBuilder{
	
	private String text;
	
	public TextSearchQueryBuilder(String text) {
		this.text = text;
	}
	
	@Override
	public String build(){
		
		return new SearchSourceBuilder()
		
			.query(QueryBuilders.boolQuery()

				// Text search for full prefix match
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName",
						"tradeName")						
					.type(Type.PHRASE)
					.boost(5))

				// Text search for prefix match
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.startsWith",
						"tradeName.startsWith")
					.type(Type.PHRASE_PREFIX)
					.boost(4))

				// Search beginning of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial",
						"tradeName.partial")					
					.boost(3))

				// Search middle of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial_middle",
						"tradeName.partial_middle")				
					.boost(1))

				// Search end of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial_back",
						"tradeName.partial_back")				
					.boost(1)))				
				.size(MAX_RESULTS)
				.toString();
	}
}
