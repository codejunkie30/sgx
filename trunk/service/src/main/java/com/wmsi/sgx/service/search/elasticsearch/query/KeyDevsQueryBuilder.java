package com.wmsi.sgx.service.search.elasticsearch.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		
		SearchSourceBuilder builder = new SearchSourceBuilder()		
		    .query(QueryBuilders
		    	.termQuery("_id", req.getTickerCode()))
		    	.size(MAX_RESULTS)
		    	.sort("keyDevs.date", SortOrder.DESC);
			
		

		if(req.getFrom() != null && req.getTo() != null)
			builder.postFilter(
			    	FilterBuilders.rangeFilter("keyDevs.date")
			    	.from(getTime(req.getFrom()))
			    	.to(getTime(req.getTo())));
		
		return builder.toString();
	}
	
	private long getTime(Object object){
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return fmt.parse(object.toString()).getTime();
		}
		catch(ParseException e){
			
			return 0;
		}
	}
}
