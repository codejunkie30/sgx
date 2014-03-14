package com.wmsi.sgx.service.search;

import java.util.List;

import com.wmsi.sgx.model.search.SearchCompany;

public interface SearchService<T>{

	List<T> search(String query) throws SearchServiceException;
	SearchCompany getById(String id) throws SearchServiceException;
}
