package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
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
				// Text search on company Name - boosted for importance
				.should(QueryBuilders.matchQuery("companyName", text)
						.analyzer("sgx_text_ngram_analyzer")
						.operator(Operator.AND)
						.boost(3))
				// Text search for tickers
				.should(QueryBuilders.matchQuery("tickerSearch", text)
						.analyzer("standard")
						.operator(Operator.AND)
						.boost(2))
				
				// Text search for prefix if only partial characters entered
				.should(QueryBuilders.multiMatchQuery(text, "companyName", "tickerSearch")
						.analyzer("standard")
						.operator(Operator.AND)
						.type(Type.PHRASE_PREFIX)))

				.size(MAX_RESULTS)
				.toString();
	}
}
