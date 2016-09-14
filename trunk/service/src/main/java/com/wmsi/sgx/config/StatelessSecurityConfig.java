package com.wmsi.sgx.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.security.CustomUserDetailsService;
import com.wmsi.sgx.security.RestAuthenticationEntryPoint;
import com.wmsi.sgx.security.RestAuthenticationFailureHandler;
import com.wmsi.sgx.security.RestAuthenticationSuccessHandler;
import com.wmsi.sgx.security.RestLogoutSuccessHandler;
import com.wmsi.sgx.security.SecureTokenGenerator;
import com.wmsi.sgx.security.token.StatelessLoginFilter;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.security.token.TransactionSessionFilter;
import com.wmsi.sgx.service.account.UserService;

import net.sf.ehcache.constructs.web.filter.GzipFilter;

@Configuration
@ComponentScan(basePackages = { "com.wmsi.sgx.security" })
@EnableWebSecurity
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class StatelessSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public DataSource dataSource;

	@Autowired
	public ObjectMapper objectMapper;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Autowired
	public RestAuthenticationEntryPoint authenticationEntry;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http

				.exceptionHandling().authenticationEntryPoint(authenticationEntry)

				.and()

				.csrf().disable()

				.formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password")
				.successHandler(authenticationSuccessHandler).failureHandler(authenticationFailureHandler)

				.and().addFilterBefore(this.statelessLoginFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(this.gzipFilter(), StatelessLoginFilter.class)
				.addFilterAfter(this.transactionFilter(), StatelessLoginFilter.class).logout().logoutUrl("/logout")
				.logoutSuccessHandler(logoutSuccessHandler);

	}

	@Autowired
	public RestLogoutSuccessHandler logoutSuccessHandler;

	@Autowired
	public RestAuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	public RestAuthenticationFailureHandler authenticationFailureHandler;

	@Bean
	public UserDetailsService userDetailsService() {
		return customUserDetailsService;
	}

	@Autowired
	private UserService userService;

	@Bean
	public DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecureTokenGenerator tokenGenerator() {
		return new SecureTokenGenerator();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	StatelessLoginFilter statelessLoginFilter() throws Exception {
		StatelessLoginFilter statelessLoginFilter = new StatelessLoginFilter(authenticationSuccessHandler,
				authenticationFailureHandler, tokenAuthenticationService, objectMapper, userService);
		statelessLoginFilter.setAuthenticationManager(this.authenticationManagerBean());
		return statelessLoginFilter;
	}

	@Bean
	GzipFilter gzipFilter() throws Exception {
		GzipFilter gzipFilter = new GzipFilter();
		return gzipFilter;
	}

	@Bean
	TransactionSessionFilter transactionFilter() throws Exception {
		TransactionSessionFilter transFilter = new TransactionSessionFilter();
		return transFilter;
	}

}
