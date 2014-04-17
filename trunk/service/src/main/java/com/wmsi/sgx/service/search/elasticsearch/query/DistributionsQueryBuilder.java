package com.wmsi.sgx.service.search.elasticsearch.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.range.RangeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.service.search.elasticsearch.StatAggregation;

public class DistributionsQueryBuilder extends AbstractQueryBuilder<Map<String, StatAggregation>>{

	private static final int MAX_RESULTS = 2000;
	
	private List<DistributionRequestField> fields;
	
	public DistributionsQueryBuilder(List<DistributionRequestField> f){
		fields = f;
	}
	
	@Override
	public SearchSourceBuilder getBuilder(Map<String, StatAggregation> ranges) {

		SearchSourceBuilder query = new SearchSourceBuilder()
			.query(QueryBuilders.constantScoreQuery(
				FilterBuilders.matchAllFilter()))
			.fetchSource(false)
			.size(MAX_RESULTS);
		
		for(DistributionRequestField req : fields){
			
			String field = req.getField();
			
			if(field.equals("industry") || field.equals("industryGroup")){
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
				
					query.aggregation(range);
				}
			}
		}	

		return query;
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
