package com.wmsi.sgx.service;

public interface PropertiesService {

	String getProperty(String key);

	void setProperty(String key, Object value, String username);

	void save();

}
