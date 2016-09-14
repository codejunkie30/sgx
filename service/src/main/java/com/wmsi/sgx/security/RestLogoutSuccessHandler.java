package com.wmsi.sgx.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TokenHandler;

@Component
public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		deleteUserTransactionTokens(request);
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().flush();

	}

	private void deleteUserTransactionTokens(HttpServletRequest request) {
		String token = request.getHeader("X-AUTH-TOKEN");
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		if (token != null && !token.isEmpty()) {
			User user = tokenHandler.parseUserFromToken(token);
			if (user != null)
				tokenAuthenticationService.clearAllTxSessionTokens(user);
		}
	}

}
