package com.wmsi.sgx.service.search.elasticsearch.query;

import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.StringUtils;

import com.wmsi.sgx.model.search.AlphaFactorSearchRequest;

public class AlphaFactorSearchQueryBuilder extends AbstractQueryBuilder<AlphaFactorSearchRequest>{

	@Override
	public SearchSourceBuilder getBuilder(AlphaFactorSearchRequest request){
	
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(getBoolFilter(request)))
			.size(1000);	
	}

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
	
	private BoolFilterBuilder addTerm(BoolFilterBuilder boolFilter, String field, Integer value){
		if(!StringUtils.isEmpty(field) && value != null){
			boolFilter.must(FilterBuilders.termFilter(field, value));
		}
		return boolFilter;
	}
}
