/**
 * 
 */
package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.WatchlistTransaction;

/**
 * Watchlist Transaction repository
 * 
 * @author dt84327
 */
public interface WatchlistTransactionRepository extends CustomRepository<WatchlistTransaction, Serializable> {

	@Query("from WatchlistTransaction where watchlist_id = :watchlist_id")
	WatchlistTransaction[] findById(@Param("watchlist_id") Long watchlist_id);
	
	@Query("from WatchlistTransaction where watchlist_id = :watchlist_id and id = :id")
	WatchlistTransaction findByIds(@Param("watchlist_id") Long watchlist_id, @Param("id") Long id);
}
