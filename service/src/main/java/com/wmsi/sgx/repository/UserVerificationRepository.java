package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserVerification;

public interface UserVerificationRepository extends CustomRepository<UserVerification, Serializable>{

	@Query("from UserVerification where token = :token")
	UserVerification findByToken(@Param("token") String token);
	
	UserVerification[] findByUser(User user);
}
