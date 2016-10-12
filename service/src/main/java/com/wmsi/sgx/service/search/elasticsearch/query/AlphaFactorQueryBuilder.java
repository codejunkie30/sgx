package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.AlphaFactor;

/**
 * 
 * This class is used to build the query related to Alpha Factors.
 *
 */
public class AlphaFactorQueryBuilder extends AbstractQueryBuilder{
	
	private static final int MAX_RESULTS = 2000;
	private List<AlphaFactor> alphas;
	
	public AlphaFactorQueryBuilder(List<AlphaFactor> alphas){
		this.alphas = alphas;
	}
	
	/**
	 * Return the query related to alpha factors.
	 * 
	 * @return String
	 */
	@Override
	public String build(){
		
		return new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.termsFilter("gvKey", getGVKeys(alphas))))
			.size(MAX_RESULTS)
			.toString();
		
	}
	
	/**
	 * Return the GV Keys based on the list of alpha factors.
	 * 
	 * @param alphas
	 *            List<AlphaFactor>
	 * @return List<String>
	 */
	private List<String> getGVKeys(List<AlphaFactor> alphas){

		List<String> gvKeys = new ArrayList<String>();
		
		for(AlphaFactor a : alphas){
			gvKeys.add(a.getCompanyId().toString());
		}

		return gvKeys;
	}
}
