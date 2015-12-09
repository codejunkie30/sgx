package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.User;

public interface UserRepository extends CustomRepository<User, Serializable>{
	
	User findByUsername(String user);
	
	@Query("from Users where created_dt > :created_dt")
	User[] findByDate(@Param("created_dt") Date date);
}
