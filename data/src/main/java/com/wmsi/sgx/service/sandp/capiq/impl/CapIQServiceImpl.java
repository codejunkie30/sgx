package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.Financials;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.CapIQServiceException;
import com.wmsi.sgx.service.sandp.capiq.InvalidIdentifierException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CompanyService companyService;

	@Override
	public Company getCompanyInfo(String id, String startDate) throws ResponseParserException, CapIQRequestException{
		return companyService.loadCompany(id,  startDate);	
	}

	@Autowired
	private FinancialsService financialsService;

	@Override
	public Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException{
		return financialsService.loadFinancials(id, currency);		
	}

	@Autowired
	private KeyDevsService keyDevsService;
	
	@Override
	public KeyDevs getKeyDevelopments(String id, String asOfDate) throws ResponseParserException, CapIQRequestException{		
		return keyDevsService.loadKeyDevelopments(id, asOfDate);
	}
	
	@Autowired
	private HoldersService holdersService;
	
	public Holders getHolderDetails(String id) throws ResponseParserException, CapIQRequestException {
		return holdersService.loadHolders(id);
	}

	@Autowired
	private HistoricalService historicalService;
	
	@Override
	public List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws ResponseParserException, CapIQRequestException {
		return historicalService.getHistoricalData(id, asOfDate);
	}
}
