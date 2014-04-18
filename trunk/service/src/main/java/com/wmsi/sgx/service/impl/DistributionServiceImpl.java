package com.wmsi.sgx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.search.elasticsearch.Aggregation;
import com.wmsi.sgx.service.search.elasticsearch.Aggregations;
import com.wmsi.sgx.service.search.elasticsearch.ESResponse;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchException;
import com.wmsi.sgx.service.search.elasticsearch.ElasticSearchService;
import com.wmsi.sgx.service.search.elasticsearch.StatAggregation;
import com.wmsi.sgx.service.search.elasticsearch.query.DistributionsQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.StatsQueryBuilder;
import com.wmsi.sgx.util.Util;

@Service
public class DistributionServiceImpl implements DistributionService{

	@Autowired
	private ElasticSearchService elasticSearchService;
	
	// TODO Externalize
	@Value("${elasticsearch.index.name}")
	private String indexName;
	
	@Autowired 
	private ModelMapper mapper;

	@Override
	@Cacheable("distributions")
	public Distributions getAggregations(DistributionsRequest req) throws ServiceException {
		
		List<DistributionRequestField> fields = req.getFields();
		Map<String, StatAggregation> intervals = getHistogramIntervals(fields);
		Aggregations aggs = getAggregations(fields, intervals);
		return (Distributions) mapper.map(aggs, Distributions.class);
	}
	
	private boolean hasNumericFields(List<DistributionRequestField> fields){
		
		for(DistributionRequestField field : fields){
			if(Util.isNumberField(SearchCompany.class, field.getField()))
				return true;
		}
		
		return false;
	}
	
	private Map<String, StatAggregation> getHistogramIntervals(List<DistributionRequestField> fields) throws ServiceException{
		
		if(!hasNumericFields(fields))
			return null;
		
		Aggregations stats = getStatsAggregations(fields);		
		Map<String, StatAggregation> aggs = new HashMap<String, StatAggregation>();
		
		for(Aggregation a : stats.getAggregations()){			
			aggs.put(a.getName(), (StatAggregation) a);			
		}	
		
		return aggs;
	}
	
	private Aggregations getAggregations(List<DistributionRequestField> fields, Map<String, StatAggregation> intervals) throws ServiceException {
		DistributionsQueryBuilder query = new DistributionsQueryBuilder(fields);
		return loadAggregations(query.build(intervals));
	}
	
	private Aggregations getStatsAggregations(List<DistributionRequestField> fields) throws ServiceException{
		return loadAggregations(new StatsQueryBuilder().build(fields));
	}

	public Aggregations loadAggregations(String query) throws ServiceException {
		
		try{			
			// TODO Externalize 'company'
			ESResponse stats = elasticSearchService.search(indexName, "company", query, new HashMap<String, Object>());
			return stats.getAggregations();
		}
		catch(ElasticSearchException e){
			throw new ServiceException("Error loading stats aggregation", e);
		}
	}
	
}