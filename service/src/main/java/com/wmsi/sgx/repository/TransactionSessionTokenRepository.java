package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.TransactionSessionVerification;
import com.wmsi.sgx.domain.User;

public interface TransactionSessionTokenRepository extends CustomRepository<TransactionSessionVerification, Serializable>
{

  @Query("from TransactionSessionVerification where user_id = :user_id")
  TransactionSessionVerification[] findByUserID(@Param("user_id") long user_id);
  
  @Query("from TransactionSessionVerification where user_id = :user_id and token = :token")
  TransactionSessionVerification findByUserIDAndToken(@Param("user_id") long user_id,@Param("token") String token);
  
  @Query("from TransactionSessionVerification where user_id = :user_id and userStatus = :userStatus")
  TransactionSessionVerification findByUserIDAndStatus(@Param("user_id") long user_id,@Param("userStatus") Long userStatus);
  
  //TransactionSessionVerification[] findByUser(User user);
}
