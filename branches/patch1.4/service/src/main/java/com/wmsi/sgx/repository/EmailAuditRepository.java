package com.wmsi.sgx.repository;

import java.io.Serializable;

import com.wmsi.sgx.domain.EmailAudit;

public interface EmailAuditRepository extends CustomRepository<EmailAudit, Serializable>{

	//TODO Change to CrudRepository if paging is not needed
	
}
