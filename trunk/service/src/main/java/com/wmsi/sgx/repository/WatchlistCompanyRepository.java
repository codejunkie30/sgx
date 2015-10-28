package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.WatchlistCompany;

public interface WatchlistCompanyRepository extends CustomRepository<WatchlistCompany, Serializable>{

	@Query("from WatchlistCompany where watchlist_id = :watchlist_id")
	WatchlistCompany[] findById(@Param("watchlist_id") Long watchlist_id);
}
