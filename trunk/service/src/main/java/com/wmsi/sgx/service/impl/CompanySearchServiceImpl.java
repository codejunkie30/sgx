package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.Criteria;
import com.wmsi.sgx.model.search.SearchCompany;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.CompanySearchService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class CompanySearchServiceImpl implements CompanySearchService{

	@Autowired
	private SearchService<List<Criteria>> companyScreenerSearchService;
	
	@Override
	@Cacheable("search")
	public SearchResults search(SearchRequest req) throws ServiceException {

		try{
			List<SearchCompany> companies = companyScreenerSearchService.search(req.getCriteria(), SearchCompany.class);
			SearchResults results = new SearchResults();
			results.setCompanies(companies);
			return results;

		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}

	@Autowired
	private SearchService<String> companyTextSearchService;
	
	@Override
	@Cacheable("search")
	public SearchResults searchCompaniesByName(CompanySearchRequest req) throws SearchServiceException{		

		List<SearchCompany> companies = companyTextSearchService.search(req.getSearch(), SearchCompany.class);

		SearchResults results = new SearchResults();
		results.setCompanies(companies);
		return results;
	}
}
