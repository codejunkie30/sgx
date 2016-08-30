package com.wmsi.sgx.security.token;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;

@Service
public class TokenAuthenticationService {

	@Autowired
	private TrasactionSessionTokenVerificationService sessionTokenVerificationSvc;

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	private static final long ONE_HOUR = 1000 * 60 * 60;

	public TokenAuthenticationService() {
		tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary(
				"9SyECk96oDsTmXfogIieDI0cD/8FpnojlYSUJT5U9I/FGVmBz5oskmjOR8cbXTvoPjX+Pq/T/b1PqpHX0lYm0oCBjXWICA=="));
	}

	public void addAuthentication(HttpServletResponse response, User user) {
		createAndAddTokenToResponseHeader(response, user);
	}

	private void createAndAddTokenToResponseHeader(HttpServletResponse response, User user) {
		user.setExpires(System.currentTimeMillis() + ONE_HOUR);
		response.addHeader(AUTH_HEADER_NAME, createToken(user));
	}

	private String createToken(User user) {
		return sessionTokenVerificationSvc.createTransactionSessionToken(user,tokenHandler.createTokenForUser(user));
	}

	public TokenHandler getTokenHandler() {
		return tokenHandler;
	}

	public int insertTransactionAuthenticationToken() {
		return 0;
	}

	public boolean validateTransactionAuthenticationToken() {
		//query the DB and check if token expires at 13th minute 
		return false;
	}

	public boolean renewTransactionAuthToken(HttpServletResponse response, User user) {
		//validate
		if(validateTransactionAuthenticationToken()){
			// disable the existing token
			// create new token
			int cnt = sessionTokenVerificationSvc.disableTransactionSessionToken(user);
			if (cnt > 0) {
				// create new token
				createAndAddTokenToResponseHeader(response, user);
				return true;
			}
		}

		return false;
	}
	
	
}
