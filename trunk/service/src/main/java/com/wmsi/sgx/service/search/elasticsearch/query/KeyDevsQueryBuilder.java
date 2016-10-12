package com.wmsi.sgx.service.search.elasticsearch.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.model.keydevs.StockListKeyDevsRequest;

/**
 * 
 * This class is used to build a Key Developments Query builder.
 *
 */
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

	/**
	 * Builds query related to Key Developments.
	 * 
	 * @return String
	 */
	@Override
	public String build() {
		SearchSourceBuilder builder = new SearchSourceBuilder();
		if (stockListKeyDevsRequest != null) {
			String[] ids = stockListKeyDevsRequest.getTickerCodes().toArray(new String[stockListKeyDevsRequest.getTickerCodes().size()]);
			builder.query(QueryBuilders.idsQuery("keyDevs").addIds(ids));

			if (stockListKeyDevsRequest.getFrom() != null && stockListKeyDevsRequest.getTo() != null) {
				builder.postFilter(FilterBuilders.boolFilter().must(FilterBuilders.rangeFilter("keyDevs.date").gt(getTime(stockListKeyDevsRequest.getFrom()))
						.lte(getTime(stockListKeyDevsRequest.getTo()))));
			}
			builder.sort("keyDevs.date", SortOrder.DESC);
		}
		return builder.toString();
	}
	
	/**
	 * Returns the time.
	 * 
	 * @param object
	 * @return long
	 */
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
