package com.wmsi.sgx.service;

import java.util.List;

import com.wmsi.sgx.model.alpha.AlphaFactorSearchRequest;

public interface AlphaFactorService{

	<T> List<T> search(AlphaFactorSearchRequest search, Class<T> clz) throws AlphaFactorServiceException;
}
