package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;

public interface AccountRepository extends CustomRepository<Account, Serializable>{
	
	List<Account> findAllByUser(User user);
	
	List<Account> findAll();

	@Query("select a from Accounts a, Users u where a.user.id = u.id and u.username = :name")
	List<Account> findByUsername(@Param("name") String username);
	
}
