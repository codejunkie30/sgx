package com.wmsi.sgx.repository;

import java.io.Serializable;

import com.wmsi.sgx.domain.Configurations;

public interface ConfigurationsRepository extends CustomRepository<Configurations, Serializable>{

	Configurations findByProperty(String property);
	
}
