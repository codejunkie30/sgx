package com.wmsi.sgx.security.token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.security.RestAuthenticationFailureHandler;
import com.wmsi.sgx.security.RestAuthenticationSuccessHandler;
import com.wmsi.sgx.service.account.UserService;
import com.wmsi.sgx.web.filter.GenericResponseWrapper;
import com.wmsi.sgx.web.filter.GetToPostRequestWrapper;
import com.wmsi.sgx.web.filter.PostRequestMapper;


public class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

	private final TokenAuthenticationService tokenAuthenticationService;
		
	private RestAuthenticationFailureHandler restAuthenticationFailureHandler;

	private RestAuthenticationSuccessHandler restAuthenticationSuccessHandler;
	
	private ObjectMapper objectMapper;
	
	private UserService userService;
	
	private Boolean userSet = false;

	com.wmsi.sgx.domain.User user;
	
	private static String token = "";


	public StatelessLoginFilter(RestAuthenticationSuccessHandler restAuthenticationSuccessHandler,
			RestAuthenticationFailureHandler restAuthenticationFailureHandler, TokenAuthenticationService tokenAuthenticationService,
			ObjectMapper objectMapper, UserService userService) {
		super(new AntPathRequestMatcher("/login", "POST"));
		this.tokenAuthenticationService = tokenAuthenticationService;
		this.restAuthenticationFailureHandler = restAuthenticationFailureHandler;
		this.restAuthenticationSuccessHandler = restAuthenticationSuccessHandler;
		this.objectMapper = objectMapper;
		this.userService = userService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		//final User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
		user = new ObjectMapper().readValue(request.getInputStream(), com.wmsi.sgx.domain.User.class);
		final UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(
				user.getUsername(), user.getPassword());
		loginToken.setDetails(authenticationDetailsSource.buildDetails(request));
		Authentication authentication = getAuthenticationManager().authenticate(loginToken);
		//SecurityContextHolder.getContext().setAuthentication(loginToken);
		return authentication;
	}
	
	@Override
	public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res,
			javax.servlet.FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		String token = request.getHeader("X-AUTH-TOKEN");
		
		TokenHandler tokenHandler = tokenAuthenticationService.getTokenHandler();
		User tyestuser = null;
		if(token != null)
		 tyestuser = tokenHandler.parseUserFromToken(token);
		if(request.getServletPath().startsWith("/purchase"))
			{super.doFilter(req, res, chain);
				return;
			}
		if(request.getServletPath().startsWith("/admin/excel"))
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

				String jsonObject = UriUtils.decode(sb.toString(), "UTF-8");

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

				String jsonObject = UriUtils.decode(sb.toString(), "UTF-8");

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

				String jsonObject = UriUtils.decode(sb.toString(), "UTF-8");

				if (jsonObject.startsWith("json="))
					jsonObject = jsonObject.replaceFirst("json=", "");

				PostRequestMapper postRequestWrapper = new PostRequestMapper(request, jsonObject,
						MediaType.APPLICATION_JSON_VALUE);

				user = (com.wmsi.sgx.domain.User) gson.fromJson(jsonObject, com.wmsi.sgx.domain.User.class);
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
			FilterChain chain, Authentication authentication) throws IOException, ServletException {

		// Lookup the complete User object from the database and create an Authentication for it
		
		//final UserDetails userDetails = userDetailService.loadUserByUsername(authentication.getName());
		final com.wmsi.sgx.domain.User user = userService.getUserByUsername(authentication.getName());
		
		//final User authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
		//final UserAuthentication userAuthentication = new UserAuthentication(user);
		// Add the authentication to the Security context
		tokenAuthenticationService.addAuthentication(response, user);
		
		logger.info("user " + user.getUsername() );
		
		
		restAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		restAuthenticationFailureHandler.onAuthenticationFailure(request, response, failed);

	}
}