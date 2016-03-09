package com.wmsi.sgx.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.FieldValue;
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
	//@Cacheable(value = "distributions", key = "#currency")
	public Distributions getAggregations(DistributionsRequest req, String currency, AccountType accType) throws ServiceException {
		
		List<DistributionRequestField> fields = req.getFields();
		Map<String, StatAggregation> intervals = getHistogramIntervals(fields, accType);
		SearchResult<Company> res = search(new DistributionsQueryBuilder(fields, intervals, accType));
		//SearchResult<Company> res = search(new DistributionsQueryBuilder(fields, intervals));
		
		Distributions dist = (Distributions) mapper.map(res.getAggregations(), Distributions.class);		
		dist.setFieldValues(getFieldValues(res, fields));
		
		return dist;
	}
	
	private boolean hasNumericFields(List<DistributionRequestField> fields){
		
		for(DistributionRequestField field : fields){
			if(Util.isNumberField(SearchCompany.class, field.getField()))
				return true;
		}
		
		return false;
	}
	
	private Map<String, StatAggregation> getHistogramIntervals(List<DistributionRequestField> fields, AccountType accType) throws ServiceException{
		
		if(!hasNumericFields(fields))
			return null;
		
		Aggregations stats = getStatsAggregations(fields, accType);		
		Map<String, StatAggregation> aggs = new HashMap<String, StatAggregation>();
		
		for(Aggregation a : stats.getAggregations()){			
			aggs.put(a.getName(), (StatAggregation) a);			
		}	
		
		return aggs;
	}
	
	private List<FieldValue> getFieldValues(SearchResult<Company> res, List<DistributionRequestField> fields) throws ServiceException {
		List<FieldValue> values = new ArrayList<FieldValue>();

		try{
			for(DistributionRequestField f : fields){

				String field = f.getField();
				
				if(!Util.isNumberField(Company.class, field))
					continue;

				FieldValue fv = new FieldValue();
				fv.setField(field);

				for(Company company : res.getHits()){
					String value = BeanUtils.getProperty(company, field);
					if(value != null)
						fv.addValue(Double.valueOf(value));
				}

				Collections.sort(fv.getValues());
				values.add(fv);

			}
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
			throw new ServiceException("Could not set field value", e);
		}

		return values;
	}
		
	private Aggregations getStatsAggregations(List<DistributionRequestField> fields, AccountType accType) throws ServiceException{
		return loadAggregations(new StatsQueryBuilder(fields,accType));
	}

	public Aggregations loadAggregations(QueryBuilder query) throws ServiceException {
		return search(query).getAggregations();
	}
	
	public SearchResult<Company> search(QueryBuilder query) throws ServiceException{
		try{			
			return companySearch.search(query, Company.class);	
		}
		catch(SearchServiceException e){
			throw new ServiceException("Error loading stats aggregation", e);
		}
	}
	
}