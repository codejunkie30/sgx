package com.wmsi.sgx.security;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wmsi.sgx.web.filter.GenericResponseWrapper;
import com.wmsi.sgx.web.filter.GetToPostRequestWrapper;
import com.wmsi.sgx.web.filter.User;

public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

	private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

	private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
	
	private ObjectMapper objectMapper;
	
	private static User user= new User();

	public CustomLoginFilter(RestAuthenticationSuccessHandler restAuthenticationSuccessHandler,
			RestAuthenticationFailureHandler restAuthenticationFailureHandler, ObjectMapper objectMapper) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.restAuthenticationFailureHandler = restAuthenticationFailureHandler;
		this.restAuthenticationSuccessHandler = restAuthenticationSuccessHandler;
		this.objectMapper = objectMapper;
	}

	

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
			Gson gson = new Gson();
			StringBuilder sb = new StringBuilder();
			String s;
			
			while((s = request.getReader().readLine()) != null){
				sb.append(s);
			}
			
			user = (User)gson.fromJson(sb.toString(), User.class);
		
		
		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getUsername(),
				user.getPassword());
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res,
			javax.servlet.FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		Map<String, String[]> parms = request.getParameterMap();
		
		//String[] string = parms.get("json");
		//StringBuilder sb = new StringBuilder();
		
		//user = objectMapper.readValue(parms.get("json")[0], User.class);
		
		if (request.getMethod() == "GET" && parms.containsKey("json") && parms.containsKey("callback")) {
			
			user = objectMapper.readValue(parms.get("json")[0], User.class);
			// Convert json query string value to request body
			GetToPostRequestWrapper postRequestWrapper = new GetToPostRequestWrapper(request, "json",
					MediaType.APPLICATION_JSON_VALUE);

			// Execute request, writing response to wrapper so we can manipulate
			// the results.
			GenericResponseWrapper wrapper = new GenericResponseWrapper(response);
			super.doFilter(postRequestWrapper, wrapper, chain);
			String callback = parms.get("callback")[0];

			// Wrap json with jsonp callback
			OutputStream out = response.getOutputStream();

			out.write((callback + "(").getBytes());
			out.write(wrapper.getData());
			out.write(");".getBytes());

			wrapper.setContentType("text/javascript;UTF-8");

			out.close();
		}else{
			
			
			super.doFilter(req, res, chain);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException, ServletException {
		restAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		restAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed);

	}

}
