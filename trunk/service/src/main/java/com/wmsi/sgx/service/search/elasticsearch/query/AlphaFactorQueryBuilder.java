package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.AlphaFactor;

public class AlphaFactorQueryBuilder extends AbstractQueryBuilder<List<AlphaFactor>>{
	
	private static final int MAX_RESULTS = 2000;
	
	public SearchSourceBuilder getBuilder(List<AlphaFactor> alphas){
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.termsFilter("gvKey", getGVKeys(alphas))))
			.size(MAX_RESULTS);
	}
	
	private List<String> getGVKeys(List<AlphaFactor> alphas){

		List<String> gvKeys = new ArrayList<String>();
		
		for(AlphaFactor a : alphas){
			gvKeys.add("GV_".concat(a.getId().substring(0,6)));
		}

		return gvKeys;
	}
}
