package com.wmsi.sgx.service;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;

public interface CompanySearchService {

	SearchResults search(SearchRequest req) throws ServiceException;

	SearchResults searchCompaniesByName(CompanySearchRequest req) throws ServiceException;

	SearchResults searchTicker(CompanySearchRequest req) throws ServiceException;

}
