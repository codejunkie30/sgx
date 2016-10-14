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

	/**
	 * Use {@link #validateTransactionAuthenticationToken(String)} instead
	 * 
	 * @throws TransactionSessionTokenVerificationException,
	 *             VerifiedTransactionSessionTokenPremiumException
	 */
	protected boolean validateTransactionAuthenticationToken(User user, String token)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException {
		return validateTransactionAuthenticationToken(token);
	}

	protected boolean validateTransactionAuthenticationToken(String token)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException {
		// check for fake tokens
		try {

			return sessionTokenVerificationSvc
					.validateTransactionSessionToken(getTokenHandler().parseUserFromToken(token), token);
		} catch (TransactionSessionTokenVerificationException | VerifiedTransactionSessionTokenPremiumException e) {
			throw e;
		}
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
	 * @throws Exception
	 * 
	 * @deprecated Use
	 *             {@link #renewTransactionAuthToken(HttpServletRequest,HttpServletResponse,User)}
	 *             instead
	 */
	public boolean renewTransactionAuthToken(HttpServletResponse response, User user) throws Exception {
		return renewTransactionAuthToken(null, response, user);
	}

	/**
	 * Validates and Creates new Transaction Authentication token. Use this API
	 * to Renew Tokens
	 * 
	 * @param request
	 * @param response
	 * @param user
	 * 
	 * @return Returns true if the token is renewed
	 * @throws TransactionSessionTokenVerificationException
	 * @throws VerifiedTransactionSessionTokenPremiumException
	 */
	public boolean renewTransactionAuthToken(HttpServletRequest request, HttpServletResponse response, User user)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException {
		// validate and expiration time and then disable the token
		String token = request.getHeader(AUTH_HEADER_NAME);
    if( isNullOrEmpty( token ) )
      throw new TransactionSessionTokenVerificationException( "Transaction token not found." );
		if (!isNullOrEmpty(token) && validateTransactionAuthenticationToken(token)
				&& sessionTokenVerificationSvc.isTokenExpiring(user, token)) {
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

	private boolean isNullOrEmpty(String myString) {
		return myString == null || "".equals(myString);
	}

}
