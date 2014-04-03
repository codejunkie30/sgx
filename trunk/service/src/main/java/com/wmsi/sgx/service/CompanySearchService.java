package com.wmsi.sgx.service;

import com.wmsi.sgx.model.search.CompanySearchRequest;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;
import com.wmsi.sgx.service.search.SearchServiceException;

public interface CompanySearchService{

	SearchResults search(SearchRequest req) throws ServiceException;

	SearchResults searchCompaniesByName(CompanySearchRequest req) throws SearchServiceException;

}
