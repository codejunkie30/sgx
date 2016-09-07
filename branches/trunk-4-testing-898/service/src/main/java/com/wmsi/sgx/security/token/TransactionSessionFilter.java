package com.wmsi.sgx.security.token;

import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.AccountModelWithAuthToken;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.UserService;

import net.sf.ehcache.constructs.web.filter.Filter;

public class TransactionSessionFilter extends Filter {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSessionFilter.class);

	@Autowired
	private TokenAuthenticationService tokenAuthSvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

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
	protected void doFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain)
			throws Throwable {

		System.out.println("doFilter of  TransactionSessionFilter");
		HttpServletRequest request = httpRequest;
		HttpServletResponse response = httpResponse;

		Map<String, String[]> parms = request.getParameterMap();

		// Check Principal Object OR AUTHENTICATION OBJECT OR Token

		String token = request.getHeader("X-AUTH-TOKEN");

		// non-logged in user
		if (token == null || token.isEmpty()) {
			chain.doFilter(request, response);
		} else {

			TokenHandler tokenHandler = tokenAuthSvc.getTokenHandler();

			if ((request.getMethod().equals("GET") || request.getMethod().equals("POST"))
					&& (request.getServletPath().equals("/reqNewTxToken"))) {
				/**
				 * validate the token -> disable the token->create new token
				 * TODO validate if token newly created and doesn't require one
				 **/
				boolean renewTransactionAuthToken = tokenAuthSvc.renewTransactionAuthToken(request, response,
						tokenHandler.parseUserFromToken(token));
				if (!renewTransactionAuthToken)
					throw new AuthenticationServiceException("Invalid Token");
				// Wrap json with jsonp callback
				objectMapper.writeValue(response.getOutputStream(),
						createAccountModel(response.getHeader("X-AUTH-TOKEN")));

			} else {
				if (!tokenAuthSvc.validateTransactionAuthenticationToken(token)) {
					// throw error message
					throw new AuthenticationServiceException("Invalid Token");
				}

				chain.doFilter(request, response);

			}

		}
	}

	private AccountModelWithAuthToken createAccountModel(String token) {
		String username = tokenAuthSvc.getTokenHandler().parseUserFromToken(token).getUsername();
		AccountModel acc = accountService.getAccountForUsername(username);
		AccountModelWithAuthToken res = new AccountModelWithAuthToken();
		res.setEmail(acc.getEmail());
		res.setCurrency(acc.getCurrency());
		res.setContactOptIn(acc.getContactOptIn());
		res.setType(acc.getType());
		res.setToken(token);
		return res;
	}
}
