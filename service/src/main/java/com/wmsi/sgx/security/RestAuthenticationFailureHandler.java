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
		//Temporary: Changing HttpServletResponse.SC_Unauthorised to SC.OK as requested for UI to recieve 
		//errors untill we implement CORS for cross domain
		response.setStatus(HttpServletResponse.SC_OK);
		AuthenticationFailure authFailure = new AuthenticationFailure(exception.getMessage());
		objectMapper.writeValue(response.getOutputStream(), authFailure);

		return;
	}

}
