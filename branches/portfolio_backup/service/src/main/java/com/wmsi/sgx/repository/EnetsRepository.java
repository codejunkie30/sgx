package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.EnetsTransactionDetails;
import com.wmsi.sgx.domain.User;

public interface EnetsRepository extends CustomRepository<EnetsTransactionDetails, Serializable>{
	
	
	List<EnetsTransactionDetails> findAllByUser(User user);
	
	@Query("from enets where active = :active and user_id=:user_id")
	List<EnetsTransactionDetails> findByUserAndActive(@Param("active") boolean active, @Param("user_id")long user_id);
	
	@Query("select u from Users u, enets e where u.id = e.user.id and trans_id = :trans_id")
	User findByTransactionId(@Param("trans_id") String trans_id); 

}
