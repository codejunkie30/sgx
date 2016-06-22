package com.wmsi.sgx.domain;

import java.io.Serializable;
import java.util.Date;

public class UserComposite implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	private String username;
	private Date date;
	
	public UserComposite(){}
	
	public UserComposite(Long id, String username, Date date){
		this.id = id;
		this.username = username;
		this.date = date;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

}
