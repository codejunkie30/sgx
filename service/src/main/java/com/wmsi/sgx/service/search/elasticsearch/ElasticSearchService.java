package com.wmsi.sgx.service.search.elasticsearch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.util.TemplateUtil;


public class ElasticSearchService{

	private ESQueryExecutor executor;

	public void setExecutor(ESQueryExecutor executor) {
		this.executor = executor;
	}

	private ObjectMapper mapper;
	public void setMapper(ObjectMapper m){
		this.mapper = m;
	}

	public <T> List<T> search(String index, String type, String query, Class<T> clz) throws ElasticSearchException{
		return search(index, type, query, new HashMap<String,Object>(), clz);		
	}
	
	public <T> List<T> search(String index, String type, String query, Map<String, Object> parms, Class<T> clz) throws ElasticSearchException{
		ESQuery esQuery = getQuery(index, type, query, parms);
		ESResponse response = query(esQuery);
		return response.getHits(clz);
	}		

	public ESResponse search(String index, String type, String query, Map<String, Object> parms) throws ElasticSearchException{
		ESQuery esQuery = getQuery(index, type, query, parms);
		return query(esQuery);
	}

	public <T> T get(String index, String type, String id, Class<T> clz ) throws ElasticSearchException{
		SourceQuery query = new SourceQuery(id);
		query.setIndex(index);
		query.setType(type);
		return executor.executeGet(query, clz);
	}

	private ESResponse query(ESQuery query) throws ElasticSearchException{
		ESResponse response = executor.executeQuery(query);
		response.setObjectMapper(mapper);
		return response;		
	}
	
	private ESQuery getQuery(String index, String type, String query, Map<String, Object> parms){
		SearchQuery esQuery = new SearchQuery();
		esQuery.setIndex(index);
		
		if(StringUtils.isNotEmpty(type))
			esQuery.setType(type);
		
		esQuery.setQueryTemplate(TemplateUtil.bind(query, parms));
		
		return esQuery;		
	}

}
