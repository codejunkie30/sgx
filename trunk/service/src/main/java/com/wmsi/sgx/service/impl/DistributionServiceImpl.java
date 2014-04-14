package com.wmsi.sgx.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.search.elasticsearch.Aggregation;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.Bucket;
import com.wmsi.sgx.service.search.elasticsearch.BucketAggregation;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.StatAggregation;

@Service
public class DistributionServiceImpl implements DistributionService{

	@Autowired
	private ElasticSearchService elasticSearchService;
	
	// TODO Externalize
	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Autowired 
	private ModelMapper mapper;

	private static final int SCALE = 1000;
	
	@Override
	@Cacheable("distributions")
	public Distributions getAggregations(DistributionsRequest req) throws ServiceException {
		
		List<DistributionRequestField> fields = req.getFields();
		Map<String, Integer> intervals = getHistogramIntervals(fields);
		Aggregations aggs = getAggregations(fields, intervals);
		return (Distributions) mapper.map(aggs, Distributions.class);
	}
	
	private Map<String, Integer> getHistogramIntervals(List<DistributionRequestField> fields) throws ServiceException{

		// TEMP - Do this externally
		List<String> rangeFields = new ArrayList<String>();
		
		for(DistributionRequestField req : fields){
			if(!req.getField().equals("industry") && !req.getField().equals("industryGroup"))
				rangeFields.add(req.getField());
		}
		/// TEMP

		// No need for the stats query if there are no numeric fields
		if(rangeFields.size() <= 0)
			return null;
		
		Aggregations stats = getStatsAggregations(rangeFields);		
		Map<String, Integer> intervals = new HashMap<String, Integer>();
		
		for(Aggregation a : stats.getAggregations()){			
			int interval = calculateInterval((StatAggregation) a);			
			intervals.put(a.getName(), interval);			
		}	
		
		return intervals;
	}
	
	private Integer calculateInterval(StatAggregation stat){
		double total = stat.getMax() - stat.getMin();
		double sqrt = Math.sqrt(stat.getCount());
		double interval = new BigDecimal(total / sqrt).doubleValue(); 

		// Scale value to remove decimals
		interval *= SCALE;

		// Round up to closest multiple of 10 for more uniform buckets
		interval = Math.round((interval + 5)/ 10) * 10;

		// round to integer
		return new BigDecimal(interval).setScale(0, RoundingMode.HALF_EVEN).intValue();
	}
	

	private Aggregations getAggregations(List<DistributionRequestField> fields, Map<String, Integer> invervals) throws ServiceException {
		try{
			String query = buildQuery(fields, invervals);
			Aggregations aggregations = loadAggregations(query);
			return normalizeAggregations(aggregations);
		}
		catch(IOException e){
			throw new ServiceException("Error creating query for stats aggregation", e);
		}
	}

	/**
	 * Elasticsearch Histogram queries are currently limited to whole numbers. We're using
	 * a scale value to multiply the number by a factor of 10 to eliminate decimals from the 
	 * histogram values. This method divides the resulting bucket keys by the given scale
	 * so the results can represent the true values. 
	 */
	private Aggregations normalizeAggregations(Aggregations aggregations){

		for(Aggregation agg : aggregations.getAggregations()){
			
			if(agg instanceof BucketAggregation){
				BucketAggregation bs = (BucketAggregation) agg;
				
				for(Bucket b : bs.getBuckets()){
					Object key = b.getKey();
					
					if(NumberUtils.isNumber(key.toString())){
						Long l = Long.valueOf(b.getKey().toString());
					
						if(l > 0){
							b.setKey( l / SCALE);
						}
					}
				}
			}
		}
		
		return aggregations;
	}
	
	private Aggregations getStatsAggregations(List<String> fields) throws ServiceException {
		try{
			String query = getStatsQuery(fields);
			return loadAggregations(query);
		}
		catch(IOException e){
			throw new ServiceException("Error creating query for stats aggregation", e);
		}		
	}

	public Aggregations loadAggregations(String query) throws ServiceException {
		
		try{			
			ESResponse stats = elasticSearchService.search(indexName, query, new HashMap<String, Object>());
			return stats.getAggregations();
		}
		catch(ElasticSearchException e){
			throw new ServiceException("Error loading stats aggregation", e);
		}
	}

	private SearchSourceBuilder getBaseQuery(){
		SearchSourceBuilder query = new SearchSourceBuilder()
		.query(
			QueryBuilders.constantScoreQuery(
				FilterBuilders.matchAllFilter())
			);
	
		query.fetchSource(false);
		query.size(2000);
		return query;
	}
	
	// TODO Externalize
	private String getStatsQuery(List<String> fields) throws IOException {

		SearchSourceBuilder query = getBaseQuery();
		
		for(String field : fields){
			query.aggregation(
				AggregationBuilders.stats(field)
				.field(field)				
			);
		}

		return query.toString();
	}

	private String buildQuery(List<DistributionRequestField> fields, Map<String, Integer> ranges) throws IOException{
		SearchSourceBuilder query = getBaseQuery();
		
		for(DistributionRequestField req : fields){
			
			String field = req.getField();
			
			// TODO Externalize
			if(field.equals("industry") || field.equals("industryGroup")){
				query.aggregation(
					AggregationBuilders.terms(field)
					.field(field)
					.size(2000)
				);
			}
			else if(ranges != null && ranges.size() > 0){
				long interval = ranges.get(field);
				
				query.aggregation(
						AggregationBuilders.histogram(field)
						.field(field)
						.interval(interval)
						.script("_value * ".concat(String.valueOf(SCALE)))						
					);
			}
		}
			
		return query.toString();
	}
}