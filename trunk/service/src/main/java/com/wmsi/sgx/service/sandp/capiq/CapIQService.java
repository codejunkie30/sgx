package com.wmsi.sgx.service.sandp.capiq;

import com.wmsi.sgx.model.CompanyInfo;

public interface CapIQService{
	CompanyInfo getCompanyInfo(String id) throws CapIQRequestException;
}