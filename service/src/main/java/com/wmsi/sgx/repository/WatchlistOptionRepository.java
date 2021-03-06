package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.WatchlistOption;

public interface WatchlistOptionRepository extends CustomRepository<WatchlistOption, Serializable>{

	
	@Query("from WatchlistOption where watchlist_id = :watchlist_id")
	WatchlistOption[] findById(@Param("watchlist_id") Long watchlist_id);
	
}