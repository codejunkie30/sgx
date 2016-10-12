package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.util.Util;

/**
 * 
 * This class is used to build a stats query builder.
 *
 */
public class StatsQueryBuilder extends AbstractQueryBuilder{
	
	private List<DistributionRequestField> fields;
	private AccountType accType;
	
	public StatsQueryBuilder(List<DistributionRequestField> f, AccountType accType){
		fields = f;
		this.accType = accType;
	}
	
	@Value("${list.permitted.exchanges}")
	private String permittedExchangesList="SGX,CATALIST";

	/**
	 * Builds query related to stats.
	 * 
	 * @return String
	 */
	@Override
	public String build() {
		
		List<String> exchangesWhiteList = new ArrayList<String>();
		for(int i=0; i<permittedExchangesList.split(",").length; i++){
			exchangesWhiteList.add(permittedExchangesList.split(",")[i]);
		}
		
		SearchSourceBuilder query=new SearchSourceBuilder();
		if(accType.equals(AccountType.PREMIUM) || accType.equals(AccountType.TRIAL)|| accType.equals(AccountType.ADMIN) || accType.equals(AccountType.MASTER)){
			query.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.matchAllFilter()))
					.fetchSource(false)
					.size(MAX_RESULTS);
			
		}else{
			query.query(QueryBuilders.constantScoreQuery(FilterBuilders.boolFilter().must(FilterBuilders.termsFilter("exchange", exchangesWhiteList))))
					.fetchSource(false)
					.size(MAX_RESULTS);
		}
		// Add aggregations
		for(DistributionRequestField req : fields){
			
			String field = req.getField();
			
			// Can only perform statics on numeric fields
			if(Util.isNumberField(SearchCompany.class, field)){
				query.aggregation(
					getAggregationFilter(field, 
						AggregationBuilders.stats(field)
						.field(field)));
			}
		}

		return query.toString();
	}
	
	/**
	 * Builds the aggregation Filter
	 * 
	 * @param field
	 *            String
	 * @param agg
	 *            AbstractAggregationBuilder
	 * @return AbstractAggregationBuilder
	 */
	private AbstractAggregationBuilder getAggregationFilter(String field, AbstractAggregationBuilder agg){

		AbstractAggregationBuilder builder = agg;
		
		if(field.equals("avgBrokerReq")){
			FilterBuilder filter = FilterBuilders
				.rangeFilter("targetPriceNum")
				.from(3);
			
			builder = AggregationBuilders
				.filter(field)
				.filter(filter).subAggregation(agg);
		}
		
		return builder;
	}

	
}
