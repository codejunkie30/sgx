package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;


import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class CompanyNameAndTickerQueryBuilder extends AbstractQueryBuilder{
	private List<String> fields = new ArrayList<String>();
	
	
	
	@Override
	public String build(){
		fields.add("companyName");
		fields.add("tickerCode");
		SearchSourceBuilder query = new SearchSourceBuilder()
				.query(QueryBuilders.constantScoreQuery(FilterBuilders.matchAllFilter())).fields(fields)
				.size(20000);
		return query.toString();
	}
}
