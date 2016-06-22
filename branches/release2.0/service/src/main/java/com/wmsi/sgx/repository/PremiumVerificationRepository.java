package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.PremiumVerification;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserVerification;

public interface PremiumVerificationRepository extends CustomRepository<PremiumVerification, Serializable>{

	@Query("from PremiumVerification where token = :token")
	PremiumVerification findByToken(@Param("token") String token);
	
	PremiumVerification[] findByUser(User user);
}
