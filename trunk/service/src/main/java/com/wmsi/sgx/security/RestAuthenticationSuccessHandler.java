package com.wmsi.sgx.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws JsonGenerationException, JsonMappingException, IOException{		
		objectMapper.writeValue(response.getOutputStream(), "");
		return;
	}
	
}
