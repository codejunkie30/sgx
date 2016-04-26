package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class IsCompanyPremiumQueryBuilder extends AbstractQueryBuilder{
	private List<String> fields = new ArrayList<String>();
	private String tickerCode;
	public IsCompanyPremiumQueryBuilder(String tickerCode){
		this.tickerCode = tickerCode;
	}
	
	@Override
	public String build(){
		fields.add("exchange");
		SearchSourceBuilder query = new SearchSourceBuilder()
				.query(QueryBuilders.constantScoreQuery(FilterBuilders.boolFilter()
						.must(FilterBuilders.termFilter("tickerCode", tickerCode)))).fields(fields)
				.size(20000);
		return query.toString();
	}
}
