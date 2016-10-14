package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.service.sandp.capiq.AbstractResponseParser;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class FinancialsResponseParser extends AbstractResponseParser{

	@Override
	public Class<Financial> getType() {
		return Financial.class;
	}
	
	/**
	 * Parse CapIQResponse to Financials
	 * @param CapIQResponse
	 * @return Financials
	 * @throws ResponseParserException
	 */
	@Override
	public Financials convert(CapIQResponse response) throws ResponseParserException {

		List<Financial> ret = new ArrayList<Financial>();

		Map<String, List<CapIQResult>> km = getKeyedResults(response);

		Iterator<Entry<String, List<CapIQResult>>> i = km.entrySet().iterator();

		while(i.hasNext()){
			Entry<String, List<CapIQResult>> entry = i.next();
			Financial financial = getFinancial(entry.getValue());

			// AbsPeriod should never be null if there's data
			if(financial != null && financial.getAbsPeriod() != null){
				ret.add(financial);
			}
		}

		Financials financials = new Financials();
		financials.setFinancials(ret);

		return financials;

	}
	
	/**
	 * Get Financial from CapIQResult List
	 * @param CapIQResult List
	 * @return Financial
	 * @throws ResponseParserException
	 */
	private Financial getFinancial(List<CapIQResult> results) throws ResponseParserException {
	
		Financial fin = new Financial();
		
		for(CapIQResult r : results){
			parseResult(r, fin);
		}
		
		return fin;
	}

	private Map<String, List<CapIQResult>> getKeyedResults(CapIQResponse response){

		Map<String, List<CapIQResult>> keyedResults = new HashMap<String, List<CapIQResult>>();

		for(CapIQResult res : response.getResults()){
			String id = res.getIdentifier();
			String period = res.getProperties().getPeriodType();
			String key = id.concat(period);

			List<CapIQResult> l = null;

			if(keyedResults.containsKey(key))
				l = keyedResults.get(key);
			else{
				l = new ArrayList<CapIQResult>();
				keyedResults.put(key, l);
			}

			l.add(res);
		}

		return keyedResults;
	}
}
