package com.wmsi.sgx.security.token;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.AccountModelWithAuthToken;
import com.wmsi.sgx.model.account.AccountModel;
import com.wmsi.sgx.security.AuthenticationFailure;
import com.wmsi.sgx.service.account.AccountService;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.UserService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

import net.sf.ehcache.constructs.web.filter.Filter;

public class TransactionSessionFilter extends Filter {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionSessionFilter.class);

	private static final String ERROR_MSG = "Invalid Token";

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

	  LOG.debug("doFilter of  TransactionSessionFilter");
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
			LOG.debug("when arrived token = = " + token);
			if ((request.getMethod().equals("GET") || request.getMethod().equals("POST"))
					&& (request.getServletPath().equals("/reqNewTxToken"))) {
				/**
				 * validate the token -> disable the token->create new token
				 * validate if token newly created and doesn't require one
				 **/
			  try {
					boolean renewTransactionAuthToken = tokenAuthSvc.renewTransactionAuthToken(request, response,
							tokenHandler.parseUserFromToken(token));
					LOG.debug("renewTransactionAuthToken = = " + renewTransactionAuthToken);
					if (renewTransactionAuthToken) {
					  LOG.debug("renew token = = " + response.getHeader("X-AUTH-TOKEN"));
					  objectMapper.writeValue(response.getOutputStream(),
								createAccountModel(response.getHeader("X-AUTH-TOKEN")));
					}
					else
					{
					  LOG.debug("not renewed token = = " + request.getHeader( "X-AUTH-TOKEN" ));
					  objectMapper.writeValue(response.getOutputStream(),
					                          createAccountModel(request.getHeader( "X-AUTH-TOKEN" )));
					  
					}
					
				} catch (TransactionSessionTokenVerificationException
						| VerifiedTransactionSessionTokenPremiumException e) {
					writeErrorToResponseStream(response);
				}

			} else if ((request.getMethod().equals("GET") || request.getMethod().equals("POST"))
					&& (request.getServletPath().equals("/logout"))) {
				User user = tokenHandler.parseUserFromToken(token);
				tokenAuthSvc.clearAllTxSessionTokens(user);
			} else {
				try {
					boolean isValidToken = tokenAuthSvc.validateTransactionAuthenticationToken(token);
					LOG.debug("isValidToken = = " + isValidToken);
					chain.doFilter(request, response);
				} catch (TransactionSessionTokenVerificationException
						| VerifiedTransactionSessionTokenPremiumException e) {
					writeErrorToResponseStream(response);
				}
			}

		}
	}

	/**
	 * @param response
	 * @throws IOException
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 */
	private void writeErrorToResponseStream(HttpServletResponse response)
			throws IOException, JsonGenerationException, JsonMappingException {
		response.setStatus(HttpServletResponse.SC_OK);
		AuthenticationFailure authFailure = new AuthenticationFailure(ERROR_MSG);
		objectMapper.writeValue(response.getOutputStream(), authFailure);
	}

	private AccountModelWithAuthToken createAccountModel(String token) {
		User userFromToken = tokenAuthSvc.getTokenHandler().parseUserFromToken(token);
		String username = userFromToken.getUsername();
		AccountModel acc = accountService.getAccountForUsername(username);
		AccountModelWithAuthToken res = new AccountModelWithAuthToken();
		res.setEmail(acc.getEmail());
		res.setCurrency(acc.getCurrency());
		res.setContactOptIn(acc.getContactOptIn());
		res.setType(acc.getType());
		res.setToken(token);
		res.setDaysRemaining(acc.getDaysRemaining());
		return res;
	}
}
