package com.wmsi.sgx.service.quanthouse.feedos;

import org.springframework.stereotype.Component;

@Component
public class FeedOSConfig{
	private String sessionName;
	public String getSessionName(){return sessionName;}
	public void setSessionName(String n){sessionName = n;}
	
	private String url;
	public String getUrl(){return url;}
	public void setUrl(String u){url = u;}
	
	private Integer port;
	public Integer getPort(){return port;}
	public void setPort(Integer p){port = p;}
	
	private String user;
	public String getUser(){return user;}
	public void setUser(String u){user = u;}
	
	private String password;
	public String getPassword(){return password;}
	public void setPassword(String p){password = p;	}
}
