package com.wmsi.sgx.service;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;

public interface KeyDevsService{
	
	KeyDevs search(KeyDevsRequest req) throws ServiceException;

}