package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CompanyService companyService;

	@Override
	public Company getCompanyInfo(String id, String startDate) throws ResponseParserException, CapIQRequestException{
		return companyService.load(id,  startDate);	
	}

	@Autowired
	private FinancialsService financialsService;

	@Override
	public Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException{
		return financialsService.load(id, currency);		
	}

	@Autowired
	private KeyDevsService keyDevsService;
	
	@Override
	public KeyDevs getKeyDevelopments(String id, String asOfDate) throws ResponseParserException, CapIQRequestException{		
		return keyDevsService.load(id, asOfDate);
	}
	
	@Autowired
	private HoldersService holdersService;
	
	public Holders getHolderDetails(String id) throws ResponseParserException, CapIQRequestException {
		return holdersService.load(id);
	}

	@Autowired
	private HistoricalService historicalService;
	
	@Override
	public PriceHistory getHistoricalData(String id, String asOfDate) throws ResponseParserException, CapIQRequestException {
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.YEAR, -5);
		return historicalService.load(id, startDate);
	}
}
