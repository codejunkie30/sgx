package com.wmsi.sgx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.distribution.DistributionRequestField;
import com.wmsi.sgx.model.distribution.Distributions;
import com.wmsi.sgx.model.distribution.DistributionsRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.service.DistributionService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.search.Aggregation;
import com.wmsi.sgx.service.search.Aggregations;
import com.wmsi.sgx.service.search.QueryBuilder;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.StatAggregation;
import com.wmsi.sgx.service.search.elasticsearch.query.DistributionsQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.StatsQueryBuilder;
import com.wmsi.sgx.util.Util;

@Service
public class DistributionServiceImpl implements DistributionService{

	@Autowired
	private SearchService companySearch;
	
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
		return loadAggregations(new DistributionsQueryBuilder(fields, intervals));		
	}
	
	private Aggregations getStatsAggregations(List<DistributionRequestField> fields) throws ServiceException{
		return loadAggregations(new StatsQueryBuilder(fields));
	}

	public Aggregations loadAggregations(QueryBuilder query) throws ServiceException {
		
		try{			
			SearchResult<?> result = companySearch.search(query, Object.class);
			return result.getAggregations();
		}
		catch(SearchServiceException e){
			throw new ServiceException("Error loading stats aggregation", e);
		}
	}
	
}