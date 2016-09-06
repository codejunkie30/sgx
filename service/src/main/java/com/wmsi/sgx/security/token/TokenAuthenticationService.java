package com.wmsi.sgx.security.token;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

@Service
public class TokenAuthenticationService {

	@Autowired
	private TrasactionSessionTokenVerificationService sessionTokenVerificationSvc;

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	
	private @Autowired AutowireCapableBeanFactory beanFactory;

	public TokenAuthenticationService() {
		tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(
				"9SyECk96oDsTmXfogIieDI0cD/8FpnojlYSUJT5U9I/FGVmBz5oskmjOR8cbXTvoPjX+Pq/T/b1PqpHX0lYm0oCBjXWICA=="));
	}

	/**
	 * addAuthentication() API should be used ONLY after successful sign-in and
	 * only once.
	 * 
	 * @param response
	 * @param user
	 */
	public void addAuthentication(HttpServletResponse response, User user) {
		beanFactory.autowireBeanProperties(tokenHandler, AutowireCapableBeanFactory.AUTOWIRE_NO, true);
		createAndAddTokenToResponseHeader(response, user);
	}

	private void createAndAddTokenToResponseHeader(HttpServletResponse response, User user) {
		user.setExpires(sessionTokenVerificationSvc.getTokenExpirationTime().getTime());
		response.addHeader(AUTH_HEADER_NAME, createToken(user));
	}

	private String createToken(User user) {
		return sessionTokenVerificationSvc.createTransactionSessionToken(user, tokenHandler.createTokenForUser(user));
	}

	public TokenHandler getTokenHandler() {
		beanFactory.autowireBeanProperties(tokenHandler, AutowireCapableBeanFactory.AUTOWIRE_NO, true);
		return tokenHandler;
	}

	protected boolean validateTransactionAuthenticationToken(User user, String token) {
		// check for fake tokens
		try {
			return sessionTokenVerificationSvc.validateTransactionSessionToken(user, token);
		} catch (TransactionSessionTokenVerificationException | VerifiedTransactionSessionTokenPremiumException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void createTxTokenB4Expiration(User user, HttpServletResponse response) {
		if (sessionTokenVerificationSvc.isTokenExpiring(user, response.getHeader(AUTH_HEADER_NAME))) {
			sessionTokenVerificationSvc.disableTransactionSessionToken(user);
			addAuthentication(response, user);
		}
	}

	/**
	 * Validates and Creates new Transaction Authentication token. Use this API
	 * to Renew Tokens
	 * 
	 * @param response
	 * @param user
	 * @return
	 * @deprecated Use {@link #renewTransactionAuthToken(HttpServletRequest,HttpServletResponse,User)} instead
	 */
	public boolean renewTransactionAuthToken(HttpServletResponse response, User user) {
		return renewTransactionAuthToken(null, response, user);
	}

	/**
	 * Validates and Creates new Transaction Authentication token. Use this API
	 * to Renew Tokens
	 * @param request TODO
	 * @param response
	 * @param user
	 * 
	 * @return
	 */
	public boolean renewTransactionAuthToken(HttpServletRequest request, HttpServletResponse response, User user) {
		// validate and expiration time and then disable the token
		if (validateTransactionAuthenticationToken(user, request.getHeader(AUTH_HEADER_NAME))) {
			// disable the existing token
			// create new token
			boolean oldDisabled = sessionTokenVerificationSvc.disableTransactionSessionToken(user);
			if (oldDisabled) {
				// create new token
				createAndAddTokenToResponseHeader(response, user);
				return true;
			}
		}

		return false;
	}

	/**
	 * Delete all session tokens of that user
	 * 
	 * @param user
	 */
	public void clearAllTxSessionTokens(User user) {
		sessionTokenVerificationSvc.deleteTransactionSessionTokens(user);
	}

}
