package com.wmsi.sgx.security.token;

import net.sf.ehcache.constructs.web.filter.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TransactionSessionFilter extends Filter {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSessionFilter.class);

	@Autowired
	private TokenAuthenticationService tokenAuthSvc;

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
	protected void doFilter(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws Exception {

		System.out.println("doFilter of  TransactionSessionFilter");
		Map<String, String[]> parms = request.getParameterMap();

		String token = request.getHeader("X-AUTH-TOKEN");

		TokenHandler tokenHandler = tokenAuthSvc.getTokenHandler();

		if ((request.getMethod() == "GET" || request.getMethod() == "POST")
				&& (parms.containsKey("callback") || parms.containsValue("jsonpCallback"))
				&& (request.getServletPath().equals("/reqNewTxToken"))) {
		/**	 validate the token -> disable the token->create new token
			 TODO validate if token newly created and doesn't require one
		 **/
			tokenAuthSvc.renewTransactionAuthToken(response, tokenHandler.parseUserFromToken(token));
		}else{
		  if(token!=null && !"".equals( token ))
		    tokenAuthSvc.createTxTokenB4Expiration(tokenHandler.parseUserFromToken(token),response);
		}
		//TODO VALIDATE IF TOKEN IS ACTIVE
		chain.doFilter(request, response);
	}

}
