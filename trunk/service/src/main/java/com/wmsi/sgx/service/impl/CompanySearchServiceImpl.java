package com.wmsi.sgx.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Account.AccountType;
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

@Service
public class CompanySearchServiceImpl implements CompanySearchService {

	@Autowired
	private SearchService companySearch;

	@Value("${list.permitted.exchanges}")
	private String permittedExchangesFile;
	
	
	@Override
	@Cacheable("searchCompany")
	public SearchResults search(SearchRequest req, AccountType accountType,String currency, String accType) throws ServiceException {

		try {
			
			return search(new CompanyQueryBuilder(req.getCriteria(), accountType));
		} catch (SearchServiceException e) {
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
