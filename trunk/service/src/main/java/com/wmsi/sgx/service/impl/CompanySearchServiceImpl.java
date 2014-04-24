package com.wmsi.sgx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.QueryBuilder;
import com.wmsi.sgx.service.search.SearchResult;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.CompanyQueryBuilder;
import com.wmsi.sgx.service.search.elasticsearch.query.TextSearchQueryBuilder;

@Service
public class CompanySearchServiceImpl implements CompanySearchService{

	@Autowired
	private SearchService companySearch;
	
	@Override
	@Cacheable("search")
	public SearchResults search(SearchRequest req) throws ServiceException {

		try{
			return search(new CompanyQueryBuilder(req.getCriteria()));
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
	@Override
	@Cacheable("search")
	public SearchResults searchCompaniesByName(CompanySearchRequest req) throws ServiceException{		

		try{
			return search(new TextSearchQueryBuilder(req.getSearch()));
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
	private SearchResults search(QueryBuilder query) throws SearchServiceException{
		SearchResult<SearchCompany> result = companySearch.search(query, SearchCompany.class);
		
		SearchResults results = new SearchResults();
		results.setCompanies(result.getHits());
		return results;
	}
}
