package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.wmsi.sgx.model.keydevs.KeyDevsRequest;

public class KeyDevsQueryBuilder extends AbstractQueryBuilder{
                
	private static final int MAX_RESULTS = 10000;
	                
	                
	private KeyDevsRequest req; 
	public KeyDevsQueryBuilder (KeyDevsRequest req) {
		this.req = req;
	}
	                
	@Override
	public String build(){
		
		return new SearchSourceBuilder()		
		    .query(QueryBuilders
		    	.termQuery("_id", req.getTickerCode()))
		    .postFilter(
		    	FilterBuilders.rangeFilter("")
		    	.to(req.getTo())
		    	.from(req.getFrom()))
		    	.size(MAX_RESULTS)
		    	.sort("date", SortOrder.DESC)
		    	.toString();
	}
}
