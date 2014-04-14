package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.model.search.AlphaFactorSearchRequest;

public interface AlphaFactorService{

	<T> List<T> search(AlphaFactorSearchRequest search, Class<T> clz) throws AlphaFactorServiceException;
}
