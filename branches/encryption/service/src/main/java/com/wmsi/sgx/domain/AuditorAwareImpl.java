package com.wmsi.sgx.domain;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.wmsi.sgx.security.UserDetailsWrapper;

@Component
public class AuditorAwareImpl implements AuditorAware<User>{

	@Override
	public User getCurrentAuditor() {

		/*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication == null){
		//if(authentication == null || !authentication.isAuthenticated()){
			return null;
		}

		User u = null;
		
		if(authentication.getPrincipal() instanceof User){
			u = ((UserDetailsWrapper) authentication.getPrincipal()).getUser();
		}
		else{*/
			// Default to system user anonymous
		User u = null;
			u = new User();
			u.setId(1L);						
		
		return u;
		
	}
}