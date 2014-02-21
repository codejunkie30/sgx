package com.wmsi.sgx.service.quanthouse.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.service.quanthouse.QuanthouseService;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;

@Service
public class QuanthouseServiceImpl implements QuanthouseService{

	@Autowired
	private FeedOSService service;
	
	private String marketExtention = "_RY";
	
	@Override
	public Double getLastPrice(String market, String id) throws QuanthouseServiceException {
		return service.getLastPrice(market, id.concat(marketExtention));
	}

}
