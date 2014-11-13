package com.wmsi.sgx.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.service.KeyDevsService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;

@Service
public class KeyDevsServiceImpl implements KeyDevsService{

	@Autowired
	private SearchService keyDevsSearch;

	@Override
	@Cacheable("keyDevsSearch")
	public KeyDevs search(KeyDevsRequest req) throws ServiceException {

		try{
			KeyDevs keyDevs = keyDevsSearch.getById(req.getTickerCode(), KeyDevs.class);
			
			List<KeyDev> filtered = new ArrayList<KeyDev>();
			
			for(KeyDev dev : keyDevs.getKeyDevs()){
				Long devDate = dev.getDate().getTime();
				
				if(devDate.compareTo(Long.valueOf(req.getFrom().toString())) > 0 
					&& devDate.compareTo(Long.valueOf(req.getTo().toString())) < 0){
					
					filtered.add(dev);
				}		
			}
			
			keyDevs.setKeyDevs(filtered);
			
			return keyDevs;
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}

}
