package com.wmsi.sgx.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.Criteria;
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
import com.wmsi.sgx.service.search.elasticsearch.query.TickerSearchQueryBuilder;
import com.wmsi.sgx.service.search.filter.FilterException;
import com.wmsi.sgx.service.search.filter.PercentChangeFilter;

@Service
public class CompanySearchServiceImpl implements CompanySearchService{

	@Autowired
	private SearchService companySearch;

	@Autowired
	private PercentChangeFilter percentChangeFilter;
	
	@Override
	@Cacheable("searchCompany")
	public SearchResults search(SearchRequest req) throws ServiceException {

		try{
			List<Criteria> criteria = req.getCriteria();

			SearchResult<Company> result = companySearch.search(new CompanyQueryBuilder(criteria), Company.class);

			// Handle percent change search using custom filter rather than elasticsearch for performance.
			List<SearchCompany> searchCompanies = percentChangeFilter.filter(result, criteria);

			SearchResults results = new SearchResults();
			results.setCompanies(searchCompanies);
			
			return results;
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
		catch(FilterException e){
			throw new ServiceException("Query filter execution failed", e);
		}
	}
	
	@Override
	@Cacheable("searchName")
	public SearchResults searchCompaniesByName(CompanySearchRequest req) throws ServiceException {

		try{
			return search(new TextSearchQueryBuilder(req.getSearch()));
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}

	@Override
	@Cacheable("searchTicker")
	public SearchResults searchTicker(CompanySearchRequest req) throws ServiceException {

		try{
			return search(new TickerSearchQueryBuilder(req.getSearch()));
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}

	private SearchResults search(QueryBuilder query) throws SearchServiceException {
		SearchResult<SearchCompany> result = companySearch.search(query, SearchCompany.class);

		SearchResults results = new SearchResults();
		results.setCompanies(result.getHits());
		return results;
	}
}
