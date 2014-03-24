package com.wmsi.sgx.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wmsi.sgx.model.search.HistogramQuery;
import com.wmsi.sgx.model.search.Query;
import com.wmsi.sgx.model.search.StatsQuery;
import com.wmsi.sgx.model.search.TermsQuery;
import com.wmsi.sgx.service.ServiceException;
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

	private ObjectMapper mapper;

	@Autowired
	public void setMapper(ObjectMapper m){mapper = m;}
	
	@Autowired
	private ElasticSearchService elasticSearchService;
	
	// TODO Externalize
	@Value("${elasticsearch.index.name}")
	private String indexName;

	
	@Override
	public Aggregations getAggregations(List<String> fields) throws ServiceException {
		
		Map<String, Integer> intervals = getHistogramIntervals(fields);
		return getAggregations(fields, intervals, 1000);
	}
	
	private Map<String, Integer> getHistogramIntervals(List<String> fields) throws ServiceException{

		// TEMP - Do this externally
		List<String> rangeFields = new ArrayList<String>();
		
		for(String s : fields){
			if(!s.equals("industry") && !s.equals("industryGroup"))
				rangeFields.add(s);
		}
		/// TEMP
		
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
		return new BigDecimal(total / sqrt).setScale(0, RoundingMode.HALF_EVEN).intValue();		
	}
	

	private Aggregations getAggregations(List<String> fields, Map<String, Integer> invervals, int scale) throws ServiceException {
		try{
			String query = buildQuery(fields, invervals, scale);
			Aggregations aggregations = loadAggregations(query);
			return normalizeAggregations(aggregations, scale);
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
	private Aggregations normalizeAggregations(Aggregations aggregations, int scale){

		for(Aggregation agg : aggregations.getAggregations()){
			
			if(agg instanceof BucketAggregation){
				BucketAggregation bs = (BucketAggregation) agg;
				
				for(Bucket b : bs.getBuckets()){
					Object key = b.getKey();
					
					if(NumberUtils.isNumber(key.toString())){
						Long l = Long.valueOf(b.getKey().toString());
					
						if(l > 0){
							b.setKey( l / scale);
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

	
	// TODO Externalize
	private String getStatsQuery(List<String> fields) throws IOException {

		List<Query> criteria = new ArrayList<Query>();
		
		for(String field : fields){
			StatsQuery query = new StatsQuery();
			query.setField(field);
			criteria.add(query);				
		}

		return buildQuery(criteria);
	}
	
	private String buildQuery(List<Query> criteria) throws IOException{

		Resource template = new ClassPathResource("META-INF/query/elasticsearch/template/constantScoreAggregators.json");
		
		ObjectNode oj = (ObjectNode) mapper.readTree(template.getFile());
		ObjectNode aggs = (ObjectNode) oj.get("aggregations");

		for(Query query: criteria){
			aggs.putPOJO(query.getField(), query);
		}

		return mapper.writeValueAsString(oj);

	}
	
	private String buildQuery(List<String> fields, Map<String, Integer> ranges, int scale) throws IOException{
		List<Query> criteria = new ArrayList<Query>();

		for(String field : fields){
			Query q = null;
			
			
			// TODO Externalize
			if(field.equals("industry") || field.equals("industryGroup")){
				TermsQuery query = new TermsQuery();
				query.setField(field);
				q = query;
			}
			else{
				HistogramQuery query = new HistogramQuery();
				query.setField(field);
				
				int interval = ranges.get(field);	
		
				query.setInterval(interval);
				query.setScale(scale);
				q = query;
			}
				
			criteria.add(q);
		}
			
		return buildQuery(criteria);
	}
	

}