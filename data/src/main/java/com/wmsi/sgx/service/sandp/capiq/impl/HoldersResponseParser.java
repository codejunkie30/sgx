package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractResponseParser;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class HoldersResponseParser extends AbstractResponseParser{

	private static final Logger log = LoggerFactory.getLogger(HoldersResponseParser.class);
	
	@Override
	public Class<Holders> getType() {
		return Holders.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Holders convert(CapIQResponse response) throws ResponseParserException {

		List<CapIQRow> names = response.getResults().get(0).getRows();
		List<CapIQRow> shares = response.getResults().get(1).getRows();
		List<CapIQRow> percent = response.getResults().get(2).getRows();

		List<Holder> ret = new ArrayList<Holder>();

		for(int i = 0; i < names.size(); i++){

			try{
				Holder h = new Holder();
				h.setName(getValue(names, i));

				if(shares.size() > i){
					h.setShares(Long.valueOf(getValue(shares, i)));
				}

				if(percent.size() > i){
					h.setPercent(Double.valueOf(getValue(percent, i)));
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
	
	private String getValue(List<CapIQRow> row, int index){
		
		String ret = null;
		
		if(row != null && row.size() >= index ){
			List<String> values = row.get(index).getValues();
			
			if(values != null && values.size() > 0)
				ret = values.get(0);
		}
		
		return ret;
		
	}
}
