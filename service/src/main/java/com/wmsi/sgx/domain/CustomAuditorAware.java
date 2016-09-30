package com.wmsi.sgx.domain;

import org.springframework.data.domain.AuditorAware;

public interface CustomAuditorAware<T> extends AuditorAware<T> {

	public User getUser();
	
	public void setUser(User user);
}
