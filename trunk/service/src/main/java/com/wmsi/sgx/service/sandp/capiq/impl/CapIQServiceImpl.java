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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.wmsi.sgx.model.CompanyInfo;
import com.wmsi.sgx.model.HistoricalValue;
import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.financials.CompanyFinancial;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQResult;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.util.MathUtil;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;
	
	// TODO Refactor - proof of concept
	@Override
	public CompanyInfo getCompanyInfo(String id, String startDate) throws CapIQRequestException {
		try{
			Resource template = new ClassPathResource("META-INF/query/capiq/companyInfo.json");
	
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			Date currentDate = df.parse(startDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			String previous =  df.format(cal.getTime());
	
			Map<String, Object> ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("currentDate", startDate);
			ctx.put("previousDate", previous);
			
			StopWatch w = new StopWatch();
			w.start();
			
			CapIQResponse response = executeCapIqRequests(template, ctx);
			
			w.stop();
			log.error("Time taken: {} ", w.getTotalTimeMillis());
			Map<String, String> m = capIqResponseToMap(response);
		
			InputStream in = new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream();
			CompanyInfo info = dozerMapper(in, m, CompanyInfo.class);
			
			template = new ClassPathResource("META-INF/query/capiq/closePrice.json");
			Date d = info.getPreviousCloseDate();
			String pre = df.format(d);
			ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("startDate", pre);
			
			CapIQResponse prevResp = executeCapIqRequests(template, ctx);
			Map<String, String> m2 = capIqResponseToMap(prevResp);
			in = new ClassPathResource("META-INF/mappings/dozer/companyInfoMapping.xml").getInputStream();
			CompanyInfo inf = dozerMapper(in, m2, CompanyInfo.class);
			
			info.setPreviousClosePrice(inf.getClosePrice());
			info.setAvgVolumeM3(getThreeMonthAvg(id, startDate));
			info.setPriceHistory(getLastYear(id, startDate));
			

			return info;
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}
	
	public Double getThreeMonthAvg(String id, String startDate) throws CapIQRequestException, ParseException {
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate = df.parse(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DAY_OF_MONTH, -91);
		String threeMonthsAgo =  df.format(cal.getTime());

		List<List<HistoricalValue>> historicalData = loadHistoricaData(id, threeMonthsAgo );
		List<HistoricalValue> volume = historicalData.get(1);
		
		Double sum = 0.0;
		
		for(HistoricalValue v : volume)
			sum += v.getValue();
			
		
		return MathUtil.avg(sum, volume.size(), 4);
	}

	public List<HistoricalValue> getLastYear(String id, String startDate) throws CapIQRequestException, ParseException {
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate = df.parse(startDate);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.YEAR, -1);
		String yearAgo =  df.format(cal.getTime());

		List<List<HistoricalValue>> historicalData = loadHistoricaData(id, yearAgo );
		return historicalData.get(0);
	}

	@Override
	public CompanyFinancial getCompanyFinancials(String id, String period) throws CapIQRequestException {
		Resource template = new ClassPathResource("META-INF/query/capiq/companyFinancials.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("period", period);
		
		CapIQResponse response = executeCapIqRequests(template, ctx);
		
		try{
			Map<String, String> m = capIqResponseToMap(response);
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
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public KeyDevs getKeyDevelopments(String id, String asOfDate) throws CapIQRequestException{
		List<String> ids = getKeyDevelopmentIds(id, asOfDate);
		
		List<KeyDev> ret = new ArrayList<KeyDev>();
		
		for(String i : ids){
			KeyDev d = getKeyDev(i);
			ret.add(d);
		}
		
		KeyDevs kd = new KeyDevs();
		kd.setTickerCode(id);
		kd.setKeyDevs(ret);
		return kd;
	}
	
	private KeyDev getKeyDev(String id) throws CapIQRequestException{
		Resource template = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		
		CapIQResponse response = executeCapIqRequests(template, ctx);
		String err = response.getErrorMsg();
		if(StringUtils.isNotEmpty(err)){
			throw new CapIQRequestException("Error response "+ err);
		}
		
		
		CapIQResult headline = response.getResults().get(0);
		CapIQResult date = response.getResults().get(1);
		CapIQResult time = response.getResults().get(2);
		CapIQResult sit = response.getResults().get(3);
		
		KeyDev keyDev = new KeyDev();
		if(headline.getRows() != null && headline.getRows().size() > 0 && headline.getRows().get(0).getValues() != null){
			keyDev.setHeadline(headline.getRows().get(0).getValues().get(1));
			keyDev.setSituation(sit.getRows().get(0).getValues().get(1));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
			try{
				keyDev.setDate(sdf.parse(date.getRows().get(0).getValues().get(1)));
			}
			catch(ParseException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return keyDev;
		
		
	}
	public List<String> getKeyDevelopmentIds(String id, String asOfDate) throws CapIQRequestException{

		Resource template = new ClassPathResource("META-INF/query/capiq/keyDevs.json");
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", getStartDate(asOfDate, Calendar.MONTH, -1));
	
		CapIQResponse response = executeCapIqRequests(template, ctx);
		String err = response.getErrorMsg();
		if(StringUtils.isNotEmpty(err)){
			throw new CapIQRequestException("Error response "+ err);
		}
		
		List<String> ids = new ArrayList<String>();
		for(CapIQResult res : response.getResults()){
			String error = res.getErrorMsg();
			if(StringUtils.isNotEmpty(error)){
				throw new CapIQRequestException("Error field "+ err);
			}
			
			for(CapIQRow r : res.getRows()){
				String idd = r.getValues().get(0);
				if(StringUtils.isNotEmpty(idd)){
					ids.add(idd);
				}
			}			
		}
		
		return ids;
	}
	
	private String getStartDate(String dt, int field, int val){
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		Date currentDate;
		try{
			currentDate = df.parse(dt);
		}
		catch(ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(field, val);
		return df.format(cal.getTime());
	}

	
	public Holders getHolderDetails(String id) throws CapIQRequestException{
		Resource template = new ClassPathResource("META-INF/query/capiq/holderDetails.json");
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
	
		CapIQResponse response = executeCapIqRequests(template, ctx);
		String err = response.getErrorMsg();
		
		if(StringUtils.isNotEmpty(err))
			return null;
		
		List<CapIQRow> names = response.getResults().get(0).getRows();
		List<CapIQRow> shares = response.getResults().get(1).getRows();
		List<CapIQRow> percent = response.getResults().get(2).getRows();
		
		List<Holder> ret = new ArrayList<Holder>();
		
		
		for(int i =0; i < names.size(); i++){
			Holder h = new Holder();
			
			h.setName(names.get(i).getValues().get(0));
		
			try{
				if(shares.size() > i){
					h.setShares(Long.valueOf(shares.get(i).getValues().get(0)));
				}
	
				if(percent.size() > i){
					h.setPercent(Double.valueOf(percent.get(i).getValues().get(0)));
				}
				ret.add(h);
			}
			catch(Exception e){
				continue;
			}
		}
		
		Holders holders = new Holders();
		holders.setHolders(ret);
		holders.setTickerCode(id);
		return holders;
	}
	
	@Override
	public List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException{
		try{
			String sDate = getStartDate(asOfDate);
			return loadHistoricaData(id, sDate);
		}
		catch(ParseException e){
			throw new CapIQRequestException(e.getMessage(), e);
		}
	}
	
	public List<List<HistoricalValue>> loadHistoricaData(String id, String startDate) throws CapIQRequestException{

		Resource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");

		try{

			Map<String, Object> ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("startDate", startDate);
		
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
	
	private Map<String, String> capIqResponseToMap(CapIQResponse response) throws Exception{
		Map<String, String> m = new HashMap<String, String>();
		
		for(CapIQResult res : response.getResults()){
			String header = res.getMnemonic();
			String err = res.getErrorMsg();
			
			if(StringUtils.isNotEmpty(err)){
				log.error("Error response {}", err);
				throw new Exception("Error " + err);				
			}
			
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
