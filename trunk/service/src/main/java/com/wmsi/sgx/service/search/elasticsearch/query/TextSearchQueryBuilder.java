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
						"companyName.full^2",
						"tradeName.full")
					.type(Type.PHRASE)
					.boost(3))
					
				// Text search for prefix match
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.startsWith^2",
						"tradeName.startsWith")
					.type(Type.PHRASE_PREFIX))

				// Search beginning of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial^2",
						"tradeName.partial")
					.boost(1))

				// Search middle of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial_middle^2",
						"tradeName.partial_middle")
					)

				// Search end of text
				.should(QueryBuilders
					.multiMatchQuery(text, 
						"companyName.partial_back^2",
						"tradeName.partial_back")
					))				
				.size(MAX_RESULTS)
				.toString();
	}
}
