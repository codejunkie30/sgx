package com.wmsi.sgx.service.search.elasticsearch.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.model.keydevs.StockListKeyDevsRequest;

public class KeyDevsQueryBuilder extends AbstractQueryBuilder {

	private static final int MAX_RESULTS = 10000;

	private KeyDevsRequest req;
	private StockListKeyDevsRequest stockListKeyDevsRequest;

	public KeyDevsQueryBuilder(KeyDevsRequest req) {
		this.req = req;

	}

	public KeyDevsQueryBuilder(StockListKeyDevsRequest req) {
		this.stockListKeyDevsRequest = req;
	}

	@Override
	public String build() {
		SearchSourceBuilder builder = new SearchSourceBuilder();
		if (stockListKeyDevsRequest != null) {
			String[] ids = stockListKeyDevsRequest.getTickerCodes().toArray(new String[stockListKeyDevsRequest.getTickerCodes().size()]);
			builder.query(QueryBuilders.idsQuery("keyDevs").addIds(ids)).sort("keyDevs.date", SortOrder.DESC);

			if (stockListKeyDevsRequest.getFrom() != null && stockListKeyDevsRequest.getTo() != null) {
				builder.postFilter(FilterBuilders.rangeFilter("keyDevs.date").from(getTime(stockListKeyDevsRequest.getFrom()))
						.to(getTime(stockListKeyDevsRequest.getTo())));
			}
		}
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
