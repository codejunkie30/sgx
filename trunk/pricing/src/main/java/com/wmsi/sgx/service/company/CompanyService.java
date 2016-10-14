package com.wmsi.sgx.service.company;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface CompanyService{
	//Get all valid ticker
	List<String> getAllTickers() throws CompanyServiceException;

}