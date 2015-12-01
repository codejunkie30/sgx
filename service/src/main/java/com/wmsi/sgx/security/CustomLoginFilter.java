package com.wmsi.sgx.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wmsi.sgx.web.filter.GenericResponseWrapper;
import com.wmsi.sgx.web.filter.GetToPostRequestWrapper;
import com.wmsi.sgx.web.filter.PostRequestMapper;
import com.wmsi.sgx.web.filter.User;

public class CustomLoginFilter extends AbstractAuthenticationProcessingFilter {

	private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

	private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;

	private ObjectMapper objectMapper;

	private static User user = new User();
	private Boolean userSet = false;

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
		if (userSet == false) {
			Gson gson = new Gson();
			StringBuilder sb = new StringBuilder();
			String s;

			while ((s = request.getReader().readLine()) != null) {
				sb.append(s);
			}
			// user = (User)gson.fromJson(sb.toString(), User.class);

			String jsonObject = java.net.URLDecoder.decode(sb.toString(), "UTF-8");

			if (jsonObject.startsWith("json="))
				jsonObject = jsonObject.replaceFirst("json=", "");

			user = (User) gson.fromJson(jsonObject, User.class);

			logger.info("User " + user);
		}
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user.getUsername(),
				user.getPassword());
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res,
			javax.servlet.FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if(request.getServletPath().startsWith("/purchase"))
			{super.doFilter(req, res, chain);
				return;
			}
		
		BufferedReader br = request.getReader();
		Map<String, String[]> parms = request.getParameterMap();
		GenericResponseWrapper wrapper = new GenericResponseWrapper(response);
		if ((request.getMethod() == "GET" || request.getMethod() == "POST")
				&& (parms.containsKey("callback") || parms.containsValue("jsonpCallback"))
				&& !(request.getServletPath().equals("/login"))) {
			
			if (request.getMethod() == "GET") {
				// Convert json query string value to post request body
				GetToPostRequestWrapper postRequestWrapper = new GetToPostRequestWrapper(request, "json",
						MediaType.APPLICATION_JSON_VALUE);
				super.doFilter(postRequestWrapper, wrapper, chain);
			} else {

				StringBuilder sb = new StringBuilder();
				String s;

				while ((s = request.getReader().readLine()) != null) {
					sb.append(s);
				}

				String jsonObject = java.net.URLDecoder.decode(sb.toString(), "UTF-8");

				if (jsonObject.startsWith("json="))
					jsonObject = jsonObject.replaceFirst("json=", "");

				PostRequestMapper postRequestWrapper = new PostRequestMapper(request, jsonObject,
						MediaType.APPLICATION_JSON_VALUE);

				super.doFilter(postRequestWrapper, wrapper, chain);
			}

			response.setContentType("text/javascript;UTF-8");
			String callback = parms.get("callback")[0];

			// Wrap json with jsonp callback
			OutputStream out = response.getOutputStream();

			out.write((callback + "(").getBytes());
			out.write(wrapper.getData());
			out.write(");".getBytes());
			// levaing this here just in case(paranoid checking)
			wrapper.setContentType("text/javascript;UTF-8");

			out.close();
		} else if ((request.getMethod() == "GET" || request.getMethod() == "POST")
					&& (!parms.containsKey("callback") || !parms.containsValue("jsonpCallback"))
					&& !(request.getServletPath().equals("/login"))
							&& !(request.getServletPath().startsWith("/purchase"))) {
				
				StringBuilder sb = new StringBuilder();
				String s;

				while ((s = br.readLine()) != null) {
					sb.append(s);
				}

				String jsonObject = java.net.URLDecoder.decode(sb.toString(), "UTF-8");

				if (jsonObject.startsWith("json="))
					jsonObject = jsonObject.replaceFirst("json=", "");

				PostRequestMapper postRequestWrapper = new PostRequestMapper(request, jsonObject,
						MediaType.APPLICATION_JSON_VALUE);

				super.doFilter(postRequestWrapper, response, chain);

			} else if ((request.getMethod() == "GET" || request.getMethod() == "POST")
					&& (!parms.containsKey("callback") || !parms.containsValue("jsonpCallback"))
					&& (request.getServletPath().equals("/login"))) {
				
				Gson gson = new Gson();				
				StringBuilder sb = new StringBuilder();
				String s;

				while ((s = br.readLine()) != null) {
					sb.append(s);
				}

				String jsonObject = java.net.URLDecoder.decode(sb.toString(), "UTF-8");

				if (jsonObject.startsWith("json="))
					jsonObject = jsonObject.replaceFirst("json=", "");

				PostRequestMapper postRequestWrapper = new PostRequestMapper(request, jsonObject,
						MediaType.APPLICATION_JSON_VALUE);

				user = (User) gson.fromJson(jsonObject, User.class);
				if(user!= null){
					userSet = true;
				}

				super.doFilter(postRequestWrapper, response, chain);
				
				OutputStream out = response.getOutputStream();

				out.write(wrapper.getData());
				

			} else {

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
