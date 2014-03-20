package com.wmsi.sgx.service;

import java.util.List;

public interface AlphaFactorService{

	<T> List<T> search(String s, Class<T> clz) throws AlphaFactorServiceException;

}
