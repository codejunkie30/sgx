package com.wmsi.sgx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wmsi.sgx.domain.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long>{
	
	Asset findByTicker(String ticker);
}
