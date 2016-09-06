package com.wmsi.sgx.security.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.wmsi.sgx.domain.User;

public class TransactionSessionFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSessionFilter.class);

	@Autowired
	private TokenAuthenticationService tokenAuthSvc;

	public TransactionSessionFilter() {
		super(new AntPathRequestMatcher("/reqNewTxToken", "POST"));
	}

	/**
	 * Performs initialization.
	 * 
	 * @param filterConfig
	 */
	protected void doInit(FilterConfig filterConfig) throws Exception {

	}

	/**
	 * A template method that performs any Filter specific destruction tasks.
	 * Called from {@link #destroy()}
	 */
	protected void doDestroy() {
		// noop
		System.out.println("doDestroy of TransactionSessionFilter");
	}

	/**
	 * Performs the filtering for a request.
	 */
	@Override
	public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res,
			javax.servlet.FilterChain chain) throws IOException, ServletException {

		System.out.println("doFilter of  TransactionSessionFilter");
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Map<String, String[]> parms = request.getParameterMap();

		String token = request.getHeader("X-AUTH-TOKEN");

		TokenHandler tokenHandler = tokenAuthSvc.getTokenHandler();

		if ((request.getMethod().equals("GET") || request.getMethod().equals("POST"))
				&& (request.getServletPath().equals("/reqNewTxToken"))) {
			/**
			 * validate the token -> disable the token->create new token TODO
			 * validate if token newly created and doesn't require one
			 **/
			boolean renewTransactionAuthToken = tokenAuthSvc.renewTransactionAuthToken(request, response,
					tokenHandler.parseUserFromToken(token));
			if (!renewTransactionAuthToken){
				tokenAuthSvc.clearAllTxSessionTokens(tokenAuthSvc.getTokenHandler().parseUserFromToken(token));
				request.logout();
				((HttpServletResponse) request).setHeader("X-AUTH-TOKEN","");
				throw new AuthenticationServiceException("Invalid Token");
			}
		}else{
			super.doFilter(request, response, chain);
		}
		/*
		 * else{ tokenAuthSvc.createTxTokenB4Expiration(tokenHandler.
		 * parseUserFromToken(token),response); }
		 */
		// TODO VALIDATE IF TOKEN IS ACTIVE
		
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String token = request.getHeader("X-AUTH-TOKEN");
		User u = tokenAuthSvc.getTokenHandler().parseUserFromToken(token);
		if (u == null)
			throw new AuthenticationServiceException("Invalid Token");
		boolean validateTransactionAuthenticationToken = tokenAuthSvc.validateTransactionAuthenticationToken(u, token);
		if (validateTransactionAuthenticationToken) {
			return new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword());
		} else{
			tokenAuthSvc.clearAllTxSessionTokens(u);
			request.logout();
			((HttpServletResponse) request).setHeader("X-AUTH-TOKEN","");
			throw new AuthenticationServiceException("Invalid Token");
		}
	}

}
