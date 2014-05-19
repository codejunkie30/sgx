package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.service.sandp.capiq.DataService;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;

@Service
public class CapIQServiceImpl implements CapIQService{

	@Autowired
	private DataService companyService;

	@Override
	public Company getCompany(String id, String startDate) throws ResponseParserException, CapIQRequestException{
		return companyService.load(id,  startDate);	
	}

	@Autowired
	private DataService financialsService;

	@Override
	public Financials getCompanyFinancials(String id, String currency) throws ResponseParserException, CapIQRequestException{
		return financialsService.load(id, currency);		
	}

	@Autowired
	private DataService keyDevsService;
	
	@Override
	public KeyDevs getKeyDevelopments(String id, String asOfDate) throws ResponseParserException, CapIQRequestException{		
		return keyDevsService.load(id, asOfDate);
	}
	
	@Autowired
	private DataService holdersService;
	
	public Holders getHolderDetails(String id) throws ResponseParserException, CapIQRequestException {
		return holdersService.load(id);
	}

	@Autowired
	private DataService historicalService;
	
	@Override
	public PriceHistory getHistoricalData(String id, String asOfDate) throws ResponseParserException, CapIQRequestException {
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.YEAR, -5);
		return historicalService.load(id, startDate, asOfDate);
	}
}
