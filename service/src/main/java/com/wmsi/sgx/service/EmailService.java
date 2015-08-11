package com.wmsi.sgx.service;

public interface EmailService{

	void send(String to, String subject, String body);

}