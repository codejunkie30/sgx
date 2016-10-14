package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.service.sandp.capiq.AbstractResponseParser;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class HoldersResponseParser extends AbstractResponseParser{

	private static final Logger log = LoggerFactory.getLogger(HoldersResponseParser.class);
	
	@Override
	public Class<Holders> getType() {
		return Holders.class;
	}
	/**
	 * Convert CapIQResponse into Holders data  
	 * @param CapIQResponse
	 * @return Holders
	 * @throws ResponseParserException
	 */
	@Override
	public Holders convert(CapIQResponse response) throws ResponseParserException {

		List<Holder> ret = new ArrayList<Holder>();

		List<String> names = getResultValues(response.getResults().get(0));
		List<String> shares = getResultValues(response.getResults().get(1));
		List<String> percent = getResultValues(response.getResults().get(2));
		
		for(int i = 0; i < names.size(); i++){
			
			try{
				Holder h = new Holder();
				BeanUtils.setProperty(h, "name", names.get(i));
				
				if(shares.size() > i && StringUtils.isNotEmpty(shares.get(i))){
					BeanUtils.setProperty(h, "shares", shares.get(i));
				}

				if(percent.size() > i && StringUtils.isNotEmpty(percent.get(i))){
					BeanUtils.setProperty(h, "percent", percent.get(i));
				}
				
				ret.add(h);
			}
			catch(Exception e){
				log.error("Exception loading holders " , e);
				continue;
			}
		}
		
		Holders holders = new Holders();
		holders.setHolders(ret);		
		return holders;
	}	
}
