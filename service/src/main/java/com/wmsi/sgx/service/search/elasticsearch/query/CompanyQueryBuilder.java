package com.wmsi.sgx.service.search.elasticsearch.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.search.Criteria;

/**
 * 
 * This class is used to build queries to fetch Company Info.
 *
 */
public class CompanyQueryBuilder extends AbstractQueryBuilder{

	private static final Logger log = LoggerFactory.getLogger(CompanyQueryBuilder.class);

	private List<Criteria> criteria;	
	private AccountType acctType;
	

	@Value("${list.permitted.exchanges}")
	private String permittedExchangesList="SGX,CATALIST";
	
	public CompanyQueryBuilder (List<Criteria> criteria, AccountType acctType) {
		this.criteria = criteria;	
		this.acctType = acctType;
	}
	
	/**
	 * Builds query to retrieve Company Info based different criteria s
	 * 
	 * @return String
	 */
	@Override
	public String build(){
		List<String> exchanges = new ArrayList<String>();
		exchanges = checkExchangeCriteria();
		// Match all if no criteria
		if(criteria == null || criteria.size() <= 0)
			return new SearchSourceBuilder()
			.query(QueryBuilders.matchAllQuery())
			.size(MAX_RESULTS)
			.toString();
		
		QueryBuilder query = null;
		FilterBuilder filter = null;
		SearchSourceBuilder builder = new SearchSourceBuilder();
		//query = QueryBuilders.boolQuery();
		
		
		
		for(Criteria c : criteria){
			if(c.getField().equals("exchange")) {
				query = addQuery(query, buildExchangeQuery(exchanges));
			}
			else if(c.getField().equals("companyOrTicker")){
				// Name query
				query = addQuery(query, buildCompanyOrTickerQuery(c));				
			}
			else if(c.getField().equals("companyName")){
				// Name query
				query = addQuery(query, buildNameQuery(c));				
			}
			else if(c.getField().equals("tickerCode")){

				// Build ticker query. Add sort for score followed by ticker
				query = addQuery(query, buildTickerCodeQuery(c));
				builder.sort("_score");
				builder.sort("tickerCode");
				
			}else if(c.getField().equals("tickerCodeExactMatch")){

				// Build ticker query. Add sort for score followed by ticker
				query = addQuery(query, buildTickerCodeExactMatchQuery(c));
				
				
			}
			else if(c.getField().equals("percentChange")){

				// Convert date strings to time
				Long to = getTime(c.getTo());
				Long from = getTime(c.getFrom());

				// Add script filter to query for calculating percentChange
				filter = addFilter(filter, buildScriptFilter(to, from, c.getValue()));
				
				// Add script field so percentChange calculation shows up in result set.
				addScriptField(builder, to, from);
				builder.fields("_source");
				
			}
			else if(c.getTo() != null || c.getFrom() != null){	
				// Range queries
				filter = addFilter(filter, buildRangeFilter(c));				
			}
			else{				
				// All other queries default to term query
				filter = addFilter(filter, buildTermFilter(c));				
			}
		}

		if(query != null)
			builder.query(query);
		
		if(filter != null)
			builder.postFilter(filter);
		
		return builder
				.size(MAX_RESULTS)
				.toString();
	}
	
	/**
	 * Builds the Query Builder based on the criteria.
	 * 
	 * @param c
	 *            Criteria
	 * @return QueryBuilder
	 */
	private QueryBuilder buildCompanyOrTickerQuery(Criteria c){

		String text = c.getValue();
		
		return QueryBuilders.boolQuery()

		// Text search for full prefix match
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.full^2",
				"tradeName.full")
			.type(Type.PHRASE)
			.boost(3))
			
		// Text search for prefix match
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.startsWith^2",
				"tradeName.startsWith")
			.type(Type.PHRASE_PREFIX))

		// Search beginning of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial^2",
				"tradeName.partial")
			.boost(1))

		// Search middle of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial_middle^2",
				"tradeName.partial_middle")
			)

		// Search end of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial_back^2",
				"tradeName.partial_back")
			)
		.should(QueryBuilders
				.constantScoreQuery(QueryBuilders
			.matchPhrasePrefixQuery("tickerCode.full", text))
			.boost(4))

		.should(QueryBuilders
				.constantScoreQuery(QueryBuilders
			.matchQuery("tickerCode.partial", text))
			.boost(2));
	}
	
	/**
	 * Adds the exchanges if they are available.
	 * 
	 * @return List<String>
	 */
	private List<String> checkExchangeCriteria(){
		
		List<String> exchanges = new ArrayList<String>();
		int noOfExchangesOnUserSerached=0;
		
		
		for (Iterator<Criteria> it =  criteria.iterator();it.hasNext();){
			Criteria c = it.next();
			if(c.getField().equals("exchange")){
				exchanges.add(c.getValue());
				it.remove();
				noOfExchangesOnUserSerached++;
			}
			
		}
			
		
		//Add the Exchange criteria back on the criteria Object after 
		//retrieving the number of exchanges on which user wants to search
		if(noOfExchangesOnUserSerached>0 || (noOfExchangesOnUserSerached==0 && (!((acctType == AccountType.PREMIUM) || (acctType == AccountType.TRIAL ) || acctType.equals(AccountType.ADMIN) || acctType.equals(AccountType.MASTER))))){
			Criteria c =  new Criteria();
			c.setField("exchange");
			criteria.add(c);
		}
		
		
		if(!((acctType == AccountType.PREMIUM) || (acctType == AccountType.TRIAL ) || acctType.equals(AccountType.ADMIN) || acctType.equals(AccountType.MASTER))){
			
			List<String> exchangesWhiteList = new ArrayList<String>();
			if(permittedExchangesList != null && permittedExchangesList.length() > 0){
				for(int i=0; i<permittedExchangesList.split(",").length; i++){
					exchangesWhiteList.add(permittedExchangesList.split(",")[i]);
				}
			}
			
			if(exchanges.size()==0){
				return exchangesWhiteList;
			}
			
			List<String> exchangesForFreeUser = new ArrayList<String>();
			//exchangesForFreeUser = exchanges;
			
			for(String ex: exchanges){
				if(exchangesWhiteList.contains(ex))
					exchangesForFreeUser.add(ex);
			}
			return exchangesForFreeUser;
			
		}else return exchanges;
		
		
	}
	
	/**
	 * Adds boolean query to the already build Query.
	 * 
	 * @param builder
	 *            QueryBuilder
	 * @param query
	 *            QueryBuilder
	 * @return QueryBuilder
	 */
	private QueryBuilder addQuery(QueryBuilder builder, QueryBuilder query){
		BoolQueryBuilder queryBuilder = (BoolQueryBuilder) builder;
		
		if(queryBuilder == null)
			queryBuilder = QueryBuilders.boolQuery();
		
		return queryBuilder.must(query);		
	}

	
	/**
	 * Adds boolean filter.
	 * 
	 * @param builder
	 *            FilterBuilder
	 * @param filter
	 *            FilterBuilder
	 * @return FilterBuilder
	 */
	private FilterBuilder addFilter(FilterBuilder builder, FilterBuilder filter){
		BoolFilterBuilder filterBuilder = (BoolFilterBuilder) builder;
		
		if(filterBuilder == null)
			filterBuilder = FilterBuilders.boolFilter();
		
		return filterBuilder.must(filter);		
	}
	
	
	/**
	 * Builds range filter.
	 * 
	 * @param c
	 *            Criteria
	 * @return FilterBuilder
	 */
	private FilterBuilder buildRangeFilter(Criteria c){
		RangeFilterBuilder rangeBuilder = FilterBuilders.rangeFilter(c.getField());
		
		if(c.getFrom() != null)
			rangeBuilder.from(c.getFrom());

		if(c.getTo() != null)
			rangeBuilder.to(c.getTo());
		
		return rangeBuilder;
	}

	
	/**
	 * Builds default filter for simple term queries.
	 * 
	 * @param c
	 *            Criteria
	 * @return FilterBuilder
	 */
	private FilterBuilder buildTermFilter(Criteria c){
		return FilterBuilders.termFilter(c.getField(), c.getValue());
	}

	
	/**
	 * Builds query using tokenizers and boosts for full and partial name
	 * matching.
	 * 
	 * @param c
	 *            Criteria
	 * @return QueryBuilder
	 */
	private QueryBuilder buildNameQuery(Criteria c){

		String text = c.getValue();
		
		return QueryBuilders.boolQuery()

		// Text search for full prefix match
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.full^2",
				"tradeName.full")
			.type(Type.PHRASE)
			.boost(3))
			
		// Text search for prefix match
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.startsWith^2",
				"tradeName.startsWith")
			.type(Type.PHRASE_PREFIX))

		// Search beginning of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial^2",
				"tradeName.partial")
			.boost(1))

		// Search middle of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial_middle^2",
				"tradeName.partial_middle")
			)

		// Search end of text
		.should(QueryBuilders
			.multiMatchQuery(text, 
				"companyName.partial_back^2",
				"tradeName.partial_back")
			);			
	}
	
	
	/**
	 * Builds query related to ticker for full and partial matching.
	 * 
	 * @param c
	 *            Criteria
	 * @return QueryBuilder
	 */
	private QueryBuilder buildTickerCodeQuery(Criteria c){
		
		String text = c.getValue();
		
		return QueryBuilders.boolQuery()
				
		// Text search for prefix match
		.should(QueryBuilders
				.constantScoreQuery(QueryBuilders
			.matchPhrasePrefixQuery("tickerCode.full", text))
			.boost(4))
	
		// Search partial matches
		.should(QueryBuilders
				.constantScoreQuery(QueryBuilders
			.matchQuery("tickerCode.partial", text))
			.boost(2));
	}
	
	/**
	 * Builds query related to ticker of exact matching.
	 * 
	 * @param c
	 *            Criteria
	 * @return QueryBuilder
	 */
	private QueryBuilder buildTickerCodeExactMatchQuery(Criteria c){
			
			String text = c.getValue();
			
			return QueryBuilders.boolQuery()
					
			// Text search for prefix match
			.should(QueryBuilders
					.constantScoreQuery(QueryBuilders
				.matchQuery("tickerCode.full", text)));
		}

	/**
	 * Builds exchange query.
	 * 
	 * @param exchanges
	 *            List<String>
	 * @return QueryBuilder
	 */
	private QueryBuilder buildExchangeQuery(List<String> exchanges){
		
		return QueryBuilders.boolQuery()
				
		// Text search for prefix match
		.should(QueryBuilders.constantScoreQuery(
				FilterBuilders.boolFilter()
				.must(FilterBuilders.termsFilter("exchange", exchanges))
				.must(FilterBuilders.typeFilter("company"))));
		}
	
	// MVEL Script fragment for collecting price history between dates on nested 'priceHistory' field 
	private static final String SCRIPT_SELECT_FRAGMENT = 
			"if(_source['priceHistory'] == null) return false; prices = ($.value in _source.priceHistory if $.date >= from && $.date <= to);";
	
	// MVEL Script for calculating and comparing percentChange from priceHistory between two dates ignoring decimals  
	private static final String SCRIPT_WHOLE = 
			SCRIPT_SELECT_FRAGMENT +
			"return prices.size() > 1 ? floor(abs((prices[prices.size()-1] - prices[0]) * 100)) == value : false";

	// MVEL Script for calculating and comparing percentChange from priceHistory between two dates comparing decimals up to 2 places 
	private static final String SCRIPT_DECIMAL = 
			SCRIPT_SELECT_FRAGMENT +
			"return prices.size() > 1 ? floor((abs((prices[prices.size()-1] - prices[0]) * 100)) * 100) == value * 100 : false";

	// MVEL Script for fragment for calculating percentChange for results display
	private static final String SCRIPT_FIELD =
			SCRIPT_SELECT_FRAGMENT +
			"return prices.size() > 1 ? (prices[prices.size()-1] - prices[0]) * 100";

	
	/**
	 * Builds script filter for calculating percent change from within nested
	 * fields.
	 * 
	 * @param to
	 *            Long
	 * @param from
	 *            Long
	 * @param val
	 *            String
	 * @return FilterBuilder
	 */
	private FilterBuilder buildScriptFilter(Long to, Long from, String val){

		String script = SCRIPT_DECIMAL;
		
		// Check if whole number or decimal. If whole number use script
		// which ignores the decimal portion so 2 would match any decimal between
		// 2.0 and 2.9
		if(Double.parseDouble(val) % 1 == 0)
			script = SCRIPT_WHOLE;
		
		return FilterBuilders
				.scriptFilter(script)
				.addParam("to", to)
				.addParam("from", from)
				.addParam("value", val);
	}

	
	/**
	 * Adds script field for percent complete so that calculation results on the
	 * nested fields show up in fields.
	 * 
	 * @param builder
	 *            SearchSourceBuilder
	 * @param to
	 *            Long
	 * @param from
	 *            Long
	 */
	private void addScriptField(SearchSourceBuilder builder, Long to, Long from){
		Map<String, Object> scriptParms = new HashMap<String, Object>();
		scriptParms.put("to", to);
		scriptParms.put("from", from);				
		builder.scriptField("percentChange", SCRIPT_FIELD, scriptParms);
	}

	
	/**
	 * Converts date to long.
	 * 
	 * @param object
	 *            Object
	 * @return long
	 */
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
