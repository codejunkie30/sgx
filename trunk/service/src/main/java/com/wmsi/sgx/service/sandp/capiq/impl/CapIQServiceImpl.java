package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.wmsi.sgx.model.CompanyFinancial;
import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;
	
	// TODO Refactor - proof of concept
	@Override
	public CompanyInfo getCompanyInfo(String id) throws CapIQRequestException {
	
		Resource template = new ClassPathResource("META-INF/query/capiq/companyInfo.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("currentDate", "02/28/2014");
		ctx.put("previousDate", "02/27/2014");
		
		StopWatch w = new StopWatch();
		w.start();
		
		CapIQResponse response = executeCapIqRequests(template, ctx);
		
		w.stop();
		log.error("Time taken: {} ", w.getTotalTimeMillis());
		Map<String, String> m = capIqResponseToMap(response);
		
		try{
			InputStream in = new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream();
			CompanyInfo info = dozerMapper(in, m, CompanyInfo.class);
			return info;
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
	
	@Override
	public CompanyFinancial getCompanyFinancials(String id, String period) throws CapIQRequestException {
		Resource template = new ClassPathResource("META-INF/query/capiq/companyFinancials.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("period", period);
		
		CapIQResponse response = executeCapIqRequests(template, ctx);
		
		Map<String, String> m = capIqResponseToMap(response);

		try{
			InputStream in = new ClassPathResource("META-INF/mappings/dozer/companyFinancialsMapping.xml").getInputStream();
			CompanyFinancial financial = dozerMapper(in, m, CompanyFinancial.class);
			
			if(financial != null){
				financial.setTickerCode(id);
				financial.setPeriod(period);
			}
			
			return financial;

		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void getKeyDevelopments(String id){
		
	}
	
	@Override
	public List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException{
		Resource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");

		try{

			Map<String, Object> ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("startDate", getStartDate(asOfDate));
		
			CapIQResponse response = executeCapIqRequests(template, ctx);
		
			CapIQResult prices = response.getResults().get(0);
			CapIQResult volumes = response.getResults().get(1);
			List<HistoricalValue> price = getHistory(prices, id);
			List<HistoricalValue> volume = getHistory(volumes,id);
			
			System.out.println(price);
			List ret = new ArrayList();
			ret.add(price);
			ret.add(volume);
			return ret;
		}
		catch(ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String getStartDate(String dt) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate = df.parse(dt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.YEAR, -5);
		return df.format(cal.getTime());
	}
	
	private List<HistoricalValue> getHistory(CapIQResult res, String id) throws ParseException{
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		List<HistoricalValue > results = new ArrayList<HistoricalValue >();
		
		// TODO sanity check headers for index of Date 
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();
			
			String v = values.get(0);
			if(NumberUtils.isNumber(v)){
				
				HistoricalValue val = new HistoricalValue();
				val.setTickerCode(id);
				val.setValue(Double.valueOf(v));
				val.setDate(df.parse(values.get(1)));
				results.add(val);
			}
		}
		
		return results;
	}
	
	private CapIQResponse executeCapIqRequests(Resource template, Map<String, Object> ctx) throws CapIQRequestException{
		String query = new CapIQRequest().parseRequest(template, ctx);
		return requestExecutor.execute(query);
	}
	
	private Map<String, String> capIqResponseToMap(CapIQResponse response){
		Map<String, String> m = new HashMap<String, String>();
		
		for(CapIQResult res : response.getResults()){
			String header = res.getMnemonic();
			String val = res.getRows().get(0).getValues().get(0);
			
			if(val != null && val.toLowerCase().startsWith("data unavailable")){
				continue;
			}
			
			if(m.containsKey(header)){
				header = header.concat("_prev");
			}
			m.put(header, val);
		}
		
		return m;		
	}

	private <T> T dozerMapper(InputStream mapping, Map m, Class<T> clz) throws IOException{
		DozerBeanMapper mapper = new DozerBeanMapper();
		mapper.addMapping(mapping);
		return mapper.map(m, clz);		
	}

}
