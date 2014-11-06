package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.search.SearchRequest;

public class KeyDevsQueryBuilder extends AbstractQueryBuilder{
                
	private static final int MAX_RESULTS = 10000;
	                
	                
	private SearchRequest req; 
	public KeyDevsQueryBuilder (SearchRequest req) {
		this.req = req;
	}
	                
	@Override
	public String build(){
		return new SearchSourceBuilder()
		    .query(QueryBuilders.matchAllQuery())
		    .postFilter(FilterBuilders.rangeFilter("")
		    .to(req.getCriteria().get(0).getTo())
		    .from(req.getCriteria().get(0).getFrom()))
		    .size(MAX_RESULTS)
		    .sort("date")
		    .toString();
	}
}
