package com.wmsi.sgx.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.service.account.UserService;

@Component
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent>{

	@Autowired
	private UserService userService;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {

		WebAuthenticationDetails auth = (WebAuthenticationDetails) event.getAuthentication().getDetails();

		UserLogin login = new UserLogin();
		login.setUsername(event.getAuthentication().getName());
		login.setIpAddress(auth.getRemoteAddress());
		login.setSuccess(true);

		userService.recordLogin(login);
	}

}
