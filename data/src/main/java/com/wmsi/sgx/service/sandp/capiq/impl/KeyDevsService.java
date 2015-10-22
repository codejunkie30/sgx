package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.integration.CompanyInputRecord;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.DateUtil;
import com.wmsi.sgx.util.TemplateUtil;

@SuppressWarnings("unchecked")
public class KeyDevsService extends AbstractDataService {

	@Override	
	public KeyDevs load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		Assert.notEmpty(parms);
		KeyDevs devs = getKeyDevelopments(id);
		devs.setTickerCode(tickerNoEx);
		return devs;
	}

	
	
	public KeyDevs getKeyDevelopments(String id)	throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		KeyDevs kD = new KeyDevs();
		kD.setTickerCode(tickerNoEx);
		Iterable<CSVRecord> records = getCompanyData(id, "key-devs");
		List<KeyDev> list = new ArrayList<KeyDev>();
		for (CSVRecord record : records) {
			KeyDev keydev = new KeyDev();
			keydev.setDate(new Date(record.get(3)));
			keydev.setHeadline(record.get(4));
			keydev.setSituation(record.get(5));
			keydev.setType(record.get(6));
			list.add(keydev);
		}
		kD.setKeyDevs(list);
		return kD;
	}
	
	
	/*private List<String> getKeyDevelopmentIds(String id, String asOfDate) throws CapIQRequestException {

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", DateUtil.adjustDate(asOfDate, Calendar.YEAR, -5));

		CapIQResponse response = requestExecutor.execute(new CapIQRequestImpl(keyDevsIdsTemplate), ctx);

		String err = response.getErrorMsg();
		if(StringUtils.isNotEmpty(err)){
			throw new CapIQRequestException("Error response " + err);
		}

		List<String> ids = new ArrayList<String>();
		for(CapIQResult res : response.getResults()){
			String error = res.getErrorMsg();
			if(StringUtils.isNotEmpty(error)){
				throw new CapIQRequestException("Error field " + err);
			}

			for(CapIQRow r : res.getRows()){
				String idd = r.getValues().get(0);
				if(StringUtils.isNotEmpty(idd) && !idd.equalsIgnoreCase("data unavailable")){
					ids.add(idd);
				}
			}
		}

		return ids;
	}*/

}
