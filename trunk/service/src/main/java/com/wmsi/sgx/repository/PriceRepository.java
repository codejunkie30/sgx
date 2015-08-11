package com.wmsi.sgx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wmsi.sgx.domain.Price;

public interface PriceRepository extends JpaRepository<Price, Long>{

}
