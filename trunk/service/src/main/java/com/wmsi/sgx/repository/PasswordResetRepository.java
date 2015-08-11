package com.wmsi.sgx.repository;

import java.io.Serializable;

import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.PasswordReset;

public interface PasswordResetRepository extends CustomRepository<PasswordReset, Serializable>{

	PasswordReset findByToken(@Param("token") String token);

}
