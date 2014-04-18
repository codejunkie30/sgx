package com.wmsi.sgx.service.search.elasticsearch.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.search.elasticsearch.StatAggregation;
import com.wmsi.sgx.util.Util;

public class DistributionsQueryBuilder extends AbstractQueryBuilder<Map<String, StatAggregation>>{

	private static final int MAX_RESULTS = 2000;
	
	private List<String> fields;
	
	public DistributionsQueryBuilder(List<DistributionRequestField> f){
	
		fields = new ArrayList<String>();
		
		for(DistributionRequestField req : f){
			fields.add(req.getField());
		}	
	}

	@Override
	public SearchSourceBuilder getBuilder(Map<String, StatAggregation> ranges) {
		
		SearchSourceBuilder query = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
					FilterBuilders.matchAllFilter()))
			.fetchSource(false)
			.size(MAX_RESULTS);
		
		for(String field : fields){
			
			// TODO Disallow tickerCode
			if(!Util.isNumberField(SearchCompany.class, field)){
				query.aggregation(
					AggregationBuilders.terms(field)
					.field(field)
					.size(2000)
				);
			}
			else if(ranges != null && ranges.size() > 0){
				StatAggregation a = ranges.get(field);
				double interval = calculateInterval(a);
				
				if(interval > 0){
					RangeBuilder range = AggregationBuilders
						.range(field)
						.field(field);
				
					for(double i = a.getMin(); i < a.getMax(); ){
						double to = i + interval;
						range.addRange(i, to);					
						i = to;
					}
					
					query.aggregation(getAggregationFilter(range, field));
				}
			}
		}	

		return query;
	}
	
	private AggregationBuilder<?> getAggregationFilter(AggregationBuilder<?> agg, String field){

		AggregationBuilder<?> builder = agg;
		
		if(field.equals("avgBrokerReq")){
			FilterBuilder filter = FilterBuilders
				.rangeFilter("targetPriceNum")
				.from(3);
			
			builder = AggregationBuilders
				.filter(field)
				.filter(filter)
				.subAggregation(agg);
		}
		
		return builder;
	}
	
	private Double calculateInterval(StatAggregation stat){
		double total = stat.getMax() - stat.getMin();
		double sqrt = Math.sqrt(stat.getCount());
		
		double ret = 0.0;
		
		if(total != 0)			
			ret = new BigDecimal(total / sqrt).doubleValue();
		
		return ret;
	}
}
