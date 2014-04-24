package com.wmsi.sgx.service.search.elasticsearch.query;

import java.util.List;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.util.Util;

public class StatsQueryBuilder extends AbstractQueryBuilder{
	
	private List<DistributionRequestField> fields;
	
	public StatsQueryBuilder(List<DistributionRequestField> f){
		fields = f;
	}

	@Override
	public String build() {
		
		SearchSourceBuilder query = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.matchAllFilter()))
			.fetchSource(false)
			.size(MAX_RESULTS);

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
	

	// TODO Move logic to common place
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
