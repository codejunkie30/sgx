package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.Watchlist;

public interface WatchlistRepository extends CustomRepository<Watchlist, Serializable>{

	@Query("from Watchlist where watchlist_id = :watchlist_id")
	Watchlist findById(@Param("watchlist_id") Long watchlist_id);
	
	Watchlist[] findByUser(User user);
	
}
