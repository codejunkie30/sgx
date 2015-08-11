package com.wmsi.sgx.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.wmsi.sgx.domain.User;

public class UserDetailsWrapper extends org.springframework.security.core.userdetails.User{

	private static final long serialVersionUID = 1L;

	private User user;
	
	private Boolean locked;
	
	public UserDetailsWrapper(User u, Collection<? extends GrantedAuthority> authorities, Boolean locked){
		super(u.getUsername(), u.getPassword(), authorities);
		user = u;
		this.locked = locked;
	}
	
	public User getUser(){
		return user;
	}

	@Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    public boolean isAccountNonLocked() {
        return !locked;
    }

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

}
