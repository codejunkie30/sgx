package com.wmsi.sgx.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Authority;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.AuthorityRepository;
import com.wmsi.sgx.service.account.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		User user = userService.getUserByUsername(username);
		
		if(user == null)
			throw new UsernameNotFoundException("User not found");
		
		Set<Authority> auths = user.getAuthorities();
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		for(Authority auth : auths)
			authorities.add(new SimpleGrantedAuthority(auth.getAuthority().toString()));

		// Check for account lock
		Boolean locked = userService.isAccountLocked(username);
		
		//Check for account expired
		Boolean expired = userService.isAccountExpired(username);
		
		return new UserDetailsWrapper(user, authorities, locked, expired);
		
	}

	
}
