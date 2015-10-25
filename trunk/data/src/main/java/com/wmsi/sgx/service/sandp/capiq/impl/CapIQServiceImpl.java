package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Company;
import com.wmsi.sgx.model.DividendHistory;
import com.wmsi.sgx.model.Estimates;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.PriceHistory;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
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
	public Company getCompany(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{
		Company company = companyService.load(input.getTicker() + ":" + input.getExchangeSymbol(),  input.getDate());
		
		if(company != null)
			company.setTradeName(input.getTradeName());
		
		return company;
	}
	

	@Autowired
	private DataService financialsService;

	@Override
	public Financials getCompanyFinancials(CompanyInputRecord input, String currency) throws ResponseParserException, CapIQRequestException{
		return financialsService.load(input.getTicker() + ":" + input.getExchangeSymbol(), currency);		
	}

	@Autowired
	private DataService keyDevsService;
	
	@Override
	public KeyDevs getKeyDevelopments(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{		
		return keyDevsService.load(input.getTicker() + ":" + input.getExchangeSymbol(), input.getDate());
	}
	
	@Autowired
	private DataService holdersService;
	
	public Holders getHolderDetails(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		return holdersService.load(input.getTicker() + ":" + input.getExchangeSymbol());
	}

	@Autowired
	private DataService historicalService;
	
	@Override
	public PriceHistory getHistoricalData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		String asOfDate = input.getDate();
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.DAY_OF_MONTH, -1835);
		return historicalService.load(input.getTicker() + ":" + input.getExchangeSymbol(), startDate, asOfDate);
	}
	
	@Autowired
	private DataService dividendService;
	
	@Override
	public DividendHistory getDividendData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		String asOfDate = input.getDate();
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.DAY_OF_MONTH, -1835);
		return dividendService.load(input.getTicker() + ":" + input.getExchangeSymbol(), startDate, asOfDate);
	}
	
	@Autowired
	private DataService estimatesService;
	
	@Override
	public Estimates getEstimates(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{
		return estimatesService.load(input.getTicker() + ":" + input.getExchangeSymbol());
	}
}
