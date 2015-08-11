package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.UserLogin;

public interface UserLoginRepository extends CustomRepository<UserLogin, Serializable>{

	Integer countByUsernameAndSuccessAndDateGreaterThan(String username, Boolean success, Date date);
	
	List<UserLogin> findByUsernameAndDateGreaterThanOrderByDateDesc(String username, Date date);
	
}
