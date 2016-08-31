package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.wmsi.sgx.domain.TransactionSessionVerification;

public interface TransactionSessionTokenRepository
		extends CustomRepository<TransactionSessionVerification, Serializable> {

	@Query("from TransactionSessionVerification where user_id = :user_id")
	TransactionSessionVerification[] findByUserID(@Param("user_id") long user_id);

	@Query("from TransactionSessionVerification where user_id = :user_id and token = :token")
	TransactionSessionVerification findByUserIDAndToken(@Param("user_id") long user_id, @Param("token") String token);

	@Query("from TransactionSessionVerification where user_id = :user_id and tx_session_token_status = :tx_session_token_status")
	TransactionSessionVerification findByUserIDAndStatus(@Param("user_id") long user_id,
			@Param("tx_session_token_status") boolean tx_session_token_status);

	@Query("from TransactionSessionVerification where user_id = :user_id and tx_session_token_status = :tx_session_token_status and token = :token  ")
	TransactionSessionVerification findByTokenUserStatus(@Param("user_id") long user_id,
			@Param("tx_session_token_status") boolean tx_session_token_status, @Param("token") String token);

	@Modifying
	@Transactional
	@Query("DELETE FROM TransactionSessionVerification where user_id= :user_id")
	int deleteUserTransactionSessionTokens(@Param("user_id") long user_id);

	// TransactionSessionVerification[] findByUser(User user);
}
