package com.wmsi.sgx.service;

import java.util.List;
import java.util.Map;

import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.StockListKeyDev;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.model.keydevs.StockListKeyDevsRequest;

public interface KeyDevsService{
	
	KeyDevs search(KeyDevsRequest req) throws ServiceException;
	List<KeyDevs> search(StockListKeyDevsRequest req) throws ServiceException;
	Map<String, List<StockListKeyDev>> searchKeyDevs(StockListKeyDevsRequest req) throws ServiceException;

}