package com.wmsi.sgx.service.search.elasticsearch.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.search.Criteria;

public class CompanyQueryBuilder extends AbstractQueryBuilder<List<Criteria>>{

	private static final Logger log = LoggerFactory.getLogger(CompanyQueryBuilder.class);
	
	private static final String SCRIPT_SELECT_FRAGMENT = 
			"prices = ($.value in _source.priceHistory if $.date >= from && $.date <= to);";
	
	private static final String SCRIPT = 
			SCRIPT_SELECT_FRAGMENT +
			"return prices.size() > 0 ? floor(abs((prices[prices.size()-1] - prices[0]) * 100)) == value : false";

	private static final String SCRIPT_FIELD =
			SCRIPT_SELECT_FRAGMENT +
			"return prices.size() > 0 ? prices[prices.size()-1] - prices[0]";
	
	private static final int MAX_RESULTS = 2000;
	
	@Override
	public SearchSourceBuilder getBuilder(List<Criteria> criteria) {

		// Match all if no criteria
		if(criteria == null || criteria.size() <= 0)
			return new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()).size(MAX_RESULTS);
		
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();		
		SearchSourceBuilder builder = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				boolFilter
				.cache(true)
				))
		.size(MAX_RESULTS);
		
		for(Criteria c : criteria){
			
			FilterBuilder filter = null;
			
			if(c.getField().equals("percentChange")){

				// Convert date strings to time
				Long to = getTime(c.getTo());
				Long from = getTime(c.getFrom());

				// Add script filter to query for calculating percentChange
				filter = buildScriptFilter(to, from, c.getValue());
				
				// Add script field so percentChange calculation shows up in result set.
				addScriptField(builder, to, from);
				builder.fields("_source");
			}
			else if(c.getTo() != null && c.getFrom() != null){
				filter = buildRangeFilter(c);
			}
			else{
				filter = buildTermFilter(c);
			}
			
			boolFilter.must(filter);
		}
		
		return builder;
	}
	
	private FilterBuilder buildRangeFilter(Criteria c){
		return FilterBuilders
				.rangeFilter(c.getField())
				.from(c.getFrom())
				.to(c.getTo());
	}

	private FilterBuilder buildTermFilter(Criteria c){
		return FilterBuilders.termFilter(c.getField(), c.getValue());
	}

	private FilterBuilder buildScriptFilter(Long to, Long from, String val){
		return FilterBuilders
				.scriptFilter(SCRIPT)
				.addParam("to", to)
				.addParam("from", from)
				.addParam("value", val);
	}

	private void addScriptField(SearchSourceBuilder builder, Long to, Long from){
		Map<String, Object> scriptParms = new HashMap<String, Object>();
		scriptParms.put("to", to);
		scriptParms.put("from", from);				
		builder.scriptField("percentChange", SCRIPT_FIELD, scriptParms);
	}
	
	private long getTime(Object object){
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return fmt.parse(object.toString()).getTime();
		}
		catch(ParseException e){
			log.error("Couldn't not parse datetime from request", e);
			return 0;
		}
	}
}
