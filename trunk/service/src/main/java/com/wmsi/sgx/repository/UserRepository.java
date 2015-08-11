package com.wmsi.sgx.repository;

import java.io.Serializable;

import com.wmsi.sgx.domain.User;

public interface UserRepository extends CustomRepository<User, Serializable>{
	
	User findByUsername(String user);
}
