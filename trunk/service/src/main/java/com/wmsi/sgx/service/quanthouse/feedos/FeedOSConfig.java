package com.wmsi.sgx.service.quanthouse.feedos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeedOSConfig{
	@Value("${quanthouse.api.sessionName}")
	private String sessionName;
	public String getSessionName(){return sessionName;}
	public void setSessionName(String n){sessionName = n;}
	
	@Value("${quanthouse.api.url}")
	private String url;
	public String getUrl(){return url;}
	public void setUrl(String u){url = u;}
	
	@Value("${quanthouse.api.port}")
	private Integer port;
	public Integer getPort(){return port;}
	public void setPort(Integer p){port = p;}
	
	@Value("${quanthouse.api.user}")
	private String user;
	public String getUser(){return user;}
	public void setUser(String u){user = u;}
	
	@Value("${quanthouse.api.password}")
	private String password;
	public String getPassword(){return password;}
	public void setPassword(String p){password = p;	}
}
