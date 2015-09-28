package com.wmsi.sgx.security;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.web.filter.GenericResponseWrapper;


@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint{
	
	@Autowired 
	private ObjectMapper mapper;
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
			throws IOException {
		
		response.setStatus(HttpServletResponse.SC_OK);
		AuthenticationFailure authFailure = new AuthenticationFailure(authException.getMessage());
		mapper.writeValue(response.getOutputStream(), authFailure);
		
		return;
	}
}