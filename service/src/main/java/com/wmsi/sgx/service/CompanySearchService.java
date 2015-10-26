package com.wmsi.sgx.service;

import com.wmsi.sgx.domain.Account.AccountType;
import com.wmsi.sgx.model.search.SearchRequest;
import com.wmsi.sgx.model.search.SearchResults;

public interface CompanySearchService {	
	
	SearchResults search(SearchRequest req, AccountType accountType) throws ServiceException;

}
