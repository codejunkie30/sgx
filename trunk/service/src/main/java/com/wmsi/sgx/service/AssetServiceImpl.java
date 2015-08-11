package com.wmsi.sgx.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Asset;
import com.wmsi.sgx.domain.Price;
import com.wmsi.sgx.repository.AssetRepository;
import com.wmsi.sgx.repository.PriceRepository;

@Service
public class AssetServiceImpl implements AssetService{

	@Autowired
	private AssetRepository repo;

	@Autowired
	private PriceRepository priceRepo;

	@Override
	@Transactional
	public List<Asset> save(List<Asset> a) {
		return repo.save(a);
	}

	@Override
	@Transactional
	public Asset save(Asset a) {
		return repo.save(a);
	}

	@Override
	@Transactional
	public Price save(Price p) {
		return priceRepo.save(p);
	}

	@Override
	@Transactional
	public Asset findByTicker(String ticker) {
		return repo.findByTicker(ticker);
	}

}
