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
			//DozerBeanMapper mapper = new DozerBeanMapper();
			//mapper.addMapping(new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream());
			//CompanyInfo info = mapper.map(m,CompanyInfo.class);
			InputStream in = new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream();
			CompanyInfo info = dozerMapper(in, m, CompanyInfo.class);
			log.error("Info {}", info);
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
	public List<HistoricalValue> getHistoricalData(String id, String asOfDate) throws CapIQRequestException{
		Resource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");

		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date lastYear = null;
		
		try{
			Date currentDate = df.parse(asOfDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.YEAR, -1);
			lastYear = cal.getTime();
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", df.format(lastYear));
		
		CapIQResponse response = executeCapIqRequests(template, ctx);
		
		CapIQResult prices = response.getResults().get(0);
		CapIQResult volumes = response.getResults().get(1);
		Map<String, String> price = getHistory(prices);
		Map<String, String> volume = getHistory(volumes);
		
		List<HistoricalValue> vals = new ArrayList<HistoricalValue>();
		
		for(Entry<String, String> e : price.entrySet()){
			
			String dt = e.getKey();
			
			if(!volume.containsKey(dt)){
				throw new RuntimeException("Missing dates in volume");
			}
			
			HistoricalValue hist = new HistoricalValue();
			hist.setTickerCode(id);
			hist.setDate(df.parse(dt));
			hist.setPrice(Double.valueOf(e.getValue()));
			hist.setVolume(Double.valueOf(volume.get(dt)));	
			vals.add(hist);
		}
		
			System.out.println(vals);
			return vals;
		}
		catch(ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private Map<String,String> getHistory(CapIQResult res){
		List<String> headers = res.getHeaders();
		List<String> rows = res.getRows().get(0).getValues();
			
		Map<String, String> results = new HashMap<String, String>();
			
		for(int i = 0; i < headers.size(); i++){
			String date = headers.get(i);
			String price = rows.get(i);
			results.put(date, price);				
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
