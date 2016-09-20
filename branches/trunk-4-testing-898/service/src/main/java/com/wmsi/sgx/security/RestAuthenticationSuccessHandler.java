package com.wmsi.sgx.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.domain.UserLogin;
import com.wmsi.sgx.model.AccountModelWithAuthToken;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.UserService;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
		
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws JsonGenerationException, JsonMappingException, IOException{		
		
		
		AccountModel acc = accountService.getAccountForUsername(findUserFromToken(response.getHeader("X-AUTH-TOKEN")).getUsername());
		AccountModelWithAuthToken res = new AccountModelWithAuthToken();
		res.setEmail(acc.getEmail());
		res.setCurrency(acc.getCurrency());
		res.setContactOptIn(acc.getContactOptIn());
		res.setType(acc.getType());
		res.setToken(response.getHeader("X-AUTH-TOKEN"));
		res.setDaysRemaining(acc.getDaysRemaining());
		
		objectMapper.writeValue(response.getOutputStream(), res);
		WebAuthenticationDetails auth = (WebAuthenticationDetails) authentication.getDetails();
		if(request.getServletPath().equals("/login")){
			UserLogin login = new UserLogin();
			login.setUsername(authentication.getName());
			login.setIpAddress(auth.getRemoteAddress());
			login.setSuccess(true);
			userService.recordLogin(login);
		}
		return;
	}
	
	public User findUserFromToken(String token){
		
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User user = null;
		if(token != null)
		 return user = tokenHandler.parseUserFromToken(token);
		return null;
	}
	
}
