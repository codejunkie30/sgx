package com.wmsi.sgx.service.search.elasticsearch.query;

/**
 * This class is used to build query builder for dividend values.
 */
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class DividendValueQueryBuilder extends AbstractQueryBuilder{
	
	private static final int MAX_RESULTS = 10000;
	
	private String id;
	
	public DividendValueQueryBuilder (String id) {
		this.id = id;
	}
	
	/**
	 * Builds the query.
	 * 
	 * @return String
	 */
	@Override
	public String build(){
		return	new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.termFilter("tickerCode",  id)))
				.fetchSource(
					new String[]{"dividendExDate", "dividendPayDate", "dividendType", "dividendPrice"}, 
					null)
			.size(MAX_RESULTS)
			.sort("dividendExDate")
			.toString();
	}
}
