package com.wmsi.sgx.security.token;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;

@Service
public class TokenAuthenticationService {

	private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

	private final TokenHandler tokenHandler;
	private static final long ONE_HOUR = 1000 * 60 * 60;

	public TokenAuthenticationService() {
		tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary("9SyECk96oDsTmXfogIieDI0cD/8FpnojlYSUJT5U9I/FGVmBz5oskmjOR8cbXTvoPjX+Pq/T/b1PqpHX0lYm0oCBjXWICA=="));
	}

	public void addAuthentication(HttpServletResponse response, User user) {
		user.setExpires(System.currentTimeMillis() + ONE_HOUR);
		response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
	}

	public TokenHandler getTokenHandler() {
		return tokenHandler;
	}
}
