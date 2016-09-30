package com.wmsi.sgx.domain;

import org.springframework.stereotype.Component;

@Component
public class AuditorAwareImpl implements CustomAuditorAware<User> {

	private User user;

	@Override
	public User getCurrentAuditor() {

		return getUser();
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

}