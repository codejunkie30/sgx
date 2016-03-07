package com.wmsi.sgx.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	//@Cacheable("keyDevsSearch")
	public KeyDevs search(KeyDevsRequest req) throws ServiceException {

		try{
			KeyDevs keyDevs = keyDevsSearch.getById(req.getTickerCode(), KeyDevs.class);
			
			List<KeyDev> filtered = new ArrayList<KeyDev>();
			
			for(KeyDev dev : keyDevs.getKeyDevs()){
				Long devDate = dev.getDate().getTime();
				
				if(req.getTo() == null){
					DateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date();
					req.setTo(dF.format(date));
				}
				if (req.getFrom() == null)
					filtered.add(dev);
				else if(devDate.compareTo(getTime(req.getFrom())) > 0 
					&& devDate.compareTo(getTime(req.getTo())) < 0){
					
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
	
	private long getTime(Object object){
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return fmt.parse(object.toString()).getTime();
		}
		catch(ParseException e){
			
			return 0;
		}
	}

}
