package com.wmsi.sgx.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class RestLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler{

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
		response.setStatus(HttpServletResponse.SC_OK);
		return;
	}
	
}
