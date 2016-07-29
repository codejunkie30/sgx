package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.wmsi.sgx.domain.Account;
import com.wmsi.sgx.domain.User;

public interface AccountRepository extends CustomRepository<Account, Serializable>{
	
	List<Account> findAllByUser(User user);
	
	List<Account> findAll();

	@Query("select a from Accounts a, Users u where a.user.id = u.id and u.username = :name")
	List<Account> findByUsername(@Param("name") String username);
	
	@Query("select a from Accounts a where a.id = :id")	
	List<Account> findAllById(@Param("id") long id);
	
	@Modifying
	@Query("update Accounts set Type = :type, Active = :active, Always_Active = :alwaysActive where user_id = :user_id")	
	@Transactional
	void updateAccountSetAdmin(@Param("type") String type, @Param("active") boolean active, @Param("alwaysActive") boolean alwaysActive, @Param("user_id")long user_id);
	
	@Modifying
	@Query("update Accounts set Active = :active, expiration_dt = :expiration_dt where user_id = :user_id")	
	@Transactional
	void updateAccountDeactivate(@Param("active") boolean active, @Param("expiration_dt") Date expiration_dt, @Param("user_id")long user_id);
	
	@Modifying
	@Query("update Accounts set expiration_dt = :expiration_dt where user_id = :user_id")	
	@Transactional
	void updateAccountExtension(@Param("expiration_dt") Date expiration_dt, @Param("user_id")long user_id);
	
	@Modifying
	@Query("update Accounts set contact_opt_in = :contact_opt_in, currency = :currency where user_id = :user_id")	
	@Transactional
	void accountserviceUpdateAccount(@Param("contact_opt_in") boolean contact_opt_in, @Param("currency") String currency, @Param("user_id")long user_id);
	
	@Modifying
	@Query("update Accounts set Active = :active where user_id = :user_id")	
	@Transactional
	void updateActive(@Param("active") boolean active, @Param("user_id")long user_id);
	

	
	
}
