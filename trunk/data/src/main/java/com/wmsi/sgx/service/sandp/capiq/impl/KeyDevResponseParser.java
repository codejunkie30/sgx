package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.service.sandp.capiq.AbstractResponseParser;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

public class KeyDevResponseParser extends AbstractResponseParser{
	
	@Override
	@SuppressWarnings("unchecked")
	public Class<KeyDev> getType() {
		return KeyDev.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	public KeyDevs convert(CapIQResponse response) throws ResponseParserException {

		List<KeyDev> ret = new ArrayList<KeyDev>();

		Map<String, List<CapIQResult>> km = parseResponse(response);

		Iterator<Entry<String, List<CapIQResult>>> i = km.entrySet().iterator();

		while(i.hasNext()){
			Entry<String, List<CapIQResult>> entry = i.next();
			KeyDev dev = getKeyDev(entry.getValue());
			ret.add(dev);
		}

		KeyDevs keyDevs = new KeyDevs();
		keyDevs.setKeyDevs(ret);
		return keyDevs;
	}

	private KeyDev getKeyDev(List<CapIQResult> results) throws ResponseParserException {

		KeyDev dev = new KeyDev();

		for(CapIQResult r : results){
			parseResult(r, dev);
		}

		return dev;
	}

	private Map<String, List<CapIQResult>> parseResponse(CapIQResponse response) {

		Map<String, List<CapIQResult>> keyedResults = new HashMap<String, List<CapIQResult>>();

		for(CapIQResult res : response.getResults()){
			String id = res.getIdentifier();

			List<CapIQResult> l = null;

			if(keyedResults.containsKey(id))
				l = keyedResults.get(id);
			else{
				l = new ArrayList<CapIQResult>();
				keyedResults.put(id, l);
			}

			l.add(res);
		}

		return keyedResults;
	}
}
