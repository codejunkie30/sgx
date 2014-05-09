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
import com.wmsi.sgx.service.search.StatAggregation;
import com.wmsi.sgx.util.Util;

public class DistributionsQueryBuilder extends AbstractQueryBuilder{

	private List<String> fields;
	private Map<String, StatAggregation> ranges;
	
	public DistributionsQueryBuilder(List<DistributionRequestField> f, Map<String, StatAggregation> r){

		ranges = r;
		fields = new ArrayList<String>();
		
		for(DistributionRequestField req : f){
			fields.add(req.getField());
		}
	}

	@Override
	public String build() {

		SearchSourceBuilder query = new SearchSourceBuilder()
				.query(QueryBuilders.constantScoreQuery(FilterBuilders.matchAllFilter())).fetchSource(false)
				.size(MAX_RESULTS);

		for(String field : fields){

			if(!Util.isNumberField(SearchCompany.class, field)){
				query.aggregation(AggregationBuilders.terms(field).field(field).size(2000));
			}
			else if(ranges != null && ranges.size() > 0){
				StatAggregation stat = ranges.get(field);
				
				AggregationBuilder<?> range = buildRangeAggregation(field, stat);

				if(range != null){
					query.aggregation(range);
				}
			}
		}

		return query.toString();
	}

	private AggregationBuilder<?> buildRangeAggregation(String field, StatAggregation stat) {
		
		double interval = calculateInterval(stat);
		double min = stat.getMin();
		double max = stat.getMax();

		if(interval <= 0)
			return null;
		
		RangeBuilder range = AggregationBuilders.range(field).field(field);

		for(double i = min; i < max;){
			
			double to = i + interval;
			
			if(to > max){			
				// Hack to make 'to' inclusive for last range so max value is returned.
				// Elasticsearch is as of version 1.1 sorely missing a way to do this with aggregations
				// other than using unbounded 'from' which won't return the max value in the results.
				to = max + 0.000000001;
			}
			
			range.addRange(i, to);
			
			i = to;
		}

		return getAggregationFilter(range, field);

	}

	private AggregationBuilder<?> getAggregationFilter(AggregationBuilder<?> agg, String field) {

		AggregationBuilder<?> builder = agg;

		if(field.equals("avgBrokerReq")){
			FilterBuilder filter = FilterBuilders.rangeFilter("targetPriceNum").from(3);

			builder = AggregationBuilders.filter(field).filter(filter).subAggregation(agg);
		}

		return builder;
	}

	private Double calculateInterval(StatAggregation stat) {
		double total = stat.getMax() - stat.getMin();
		double sqrt = Math.sqrt(stat.getCount());

		double ret = 0.0;

		if(total != 0)
			ret = new BigDecimal(total / sqrt).doubleValue();

		return ret;
	}

}
