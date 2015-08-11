package com.wmsi.sgx.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	@Autowired
	private ObjectMapper objectMapper;

	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		objectMapper.writeValue(response.getOutputStream(), new AuthenticationFailure(exception.getMessage()));

		return;
	}

	class AuthenticationFailure{

		private String reason;

		public AuthenticationFailure(String r){
			reason = r;
		}

		public String getReason() {
			return reason;
		}

		public void setReason(String reason) {
			this.reason = reason;
		}

	}

}
