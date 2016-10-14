package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private DataService companyService;
	
	/**
	 * Get CompanyData based on CompanyInputRecord 
	 * @param CompanyInputRecord
	 * @return Company
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Company getCompany(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{
		log.debug("Company Input Record Values"+input.getTicker() + ":" + input.getExchangeSymbol(),  input.getDate());
		Company company = companyService.load(input.getTicker() + ":" + input.getExchangeSymbol(),  input.getDate());
		
		if(company != null)
			company.setTradeName(input.getTradeName());
		
		return company;
	}
	

	@Autowired
	private DataService financialsService;
	
	/**
	 * Get financials data based on CompanyInputRecord
	 * @param CompanyInputRecord
	 * @param currency 
	 * @return Financials
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Financials getCompanyFinancials(CompanyInputRecord input, String currency) throws ResponseParserException, CapIQRequestException{
		return financialsService.load(input.getTicker() + ":" + input.getExchangeSymbol(), currency);		
	}

	@Autowired
	private DataService keyDevsService;
	
	/**
	 * Get keyDevelopments data based on CompanyInputRecord
	 * @param CompanyInputRecord
	 * @return Holders
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public KeyDevs getKeyDevelopments(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{		
		return keyDevsService.load(input.getTicker() + ":" + input.getExchangeSymbol(), input.getDate());
	}
	
	@Autowired
	private DataService holdersService;
	
	/**
	 * Get holders ownership data based on CompanyInputRecord
	 * @param CompanyInputRecord
	 * @return Holders
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public Holders getHolderDetails(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		return holdersService.load(input.getTicker() + ":" + input.getExchangeSymbol());
	}

	@Autowired
	private DataService historicalService;
	
	/**
	 * Get historical Pricing data 
	 * @param CompanyInputRecord
	 * @return PriceHistory
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public PriceHistory getHistoricalData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		String asOfDate = input.getDate();
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.DAY_OF_MONTH, -1835);
		return historicalService.load(input.getTicker() + ":" + input.getExchangeSymbol(), startDate, asOfDate);
	}
	
	@Autowired
	private DataService dividendService;
	
	/**
	 * Get Dividend data 
	 * @param CompanyInputRecord
	 * @return DividendData
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public DividendHistory getDividendData(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException {
		String asOfDate = input.getDate();
		String startDate = DateUtil.adjustDate(asOfDate, Calendar.DAY_OF_MONTH, -1835);
		return dividendService.load(input.getTicker() + ":" + input.getExchangeSymbol(), startDate, asOfDate);
	}
	
	@Autowired
	private DataService estimatesService;
	
	/**
	 * Get Estimates data 
	 * @param CompanyInputRecord
	 * @return Estimates data
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Estimates getEstimates(CompanyInputRecord input) throws ResponseParserException, CapIQRequestException{
		return estimatesService.load(input.getTicker() + ":" + input.getExchangeSymbol());
	}
}
