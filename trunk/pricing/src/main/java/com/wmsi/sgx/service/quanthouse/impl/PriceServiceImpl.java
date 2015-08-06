package com.wmsi.sgx.service.quanthouse.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Price;
import com.wmsi.sgx.repositories.PriceRepository;
import com.wmsi.sgx.service.quanthouse.PriceService;

@Service
public class PriceServiceImpl implements PriceService{

	
	@Autowired
	private PriceRepository priceRepository;
	
	@Override
	@Transactional
	public void savePrice(Price p){
		priceRepository.save(p);
		
	}
	
	@Override
	public Price getPrice(String market, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Price> getPriceHistory(String market, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
