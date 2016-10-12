package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.model.search.AlphaFactorSearchRequest;

/**
 * 
 * This class is used to build the query based on the various filter criteria.
 *
 */
public class AlphaFactorSearchQueryBuilder extends AbstractQueryBuilder{
	
	private AlphaFactorSearchRequest request;
	
	public AlphaFactorSearchQueryBuilder (AlphaFactorSearchRequest request){
		this.request = request;
	}
	
	/**
	 * Builds the query based on the filter criteria encapsulated in the
	 * request.
	 * 
	 * @return String
	 */
	public String build(){
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(getBoolFilter(request)))
			.size(MAX_RESULTS)
			.toString();	
	}

	/**
	 * AddS different criteria.
	 * 
	 * @param req
	 *            AlphaFactorSearchRequest
	 * @return BoolFilterBuilder
	 */
	private BoolFilterBuilder getBoolFilter(AlphaFactorSearchRequest req){
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter();
		addTerm(boolFilter, "analystExpectations", req.getAnalystExpectations());
		addTerm(boolFilter, "capitalEfficiency", req.getCapitalEfficiency());
		addTerm(boolFilter, "earningsQuality", req.getEarningsQuality());
		addTerm(boolFilter, "historicalGrowth", req.getHistoricalGrowth());
		addTerm(boolFilter, "priceMomentum", req.getPriceMomentum());
		addTerm(boolFilter, "size", req.getSize());
		addTerm(boolFilter, "valuation", req.getValuation());
		addTerm(boolFilter, "volatility", req.getVolatility());
		return boolFilter;
	}
	
	/**
	 * Tests the criteria is there or not, if it is available then it will be
	 * added.
	 * 
	 * @param boolFilter
	 *            BoolFilterBuilder
	 * @param field
	 *            String
	 * @param value
	 *            Integer
	 * @return BoolFilterBuilder
	 */
	private BoolFilterBuilder addTerm(BoolFilterBuilder boolFilter, String field, Integer value){
		if(!StringUtils.isEmpty(field) && value != null){
			boolFilter.must(FilterBuilders.termFilter(field, value));
		}
		return boolFilter;
	}
}
