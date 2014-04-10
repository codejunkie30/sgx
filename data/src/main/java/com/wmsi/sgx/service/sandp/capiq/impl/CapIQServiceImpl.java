package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

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
import com.wmsi.sgx.service.conversion.ModelMapper;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;
import com.wmsi.sgx.service.sandp.capiq.CapIQService;
import com.wmsi.sgx.util.DateUtil;

@Service
public class CapIQServiceImpl implements CapIQService{

	private Logger log = LoggerFactory.getLogger(CapIQServiceImpl.class);

	@Autowired
	private CapIQRequestExecutor requestExecutor;

	private ModelMapper mapper;

	@Autowired
	public void setMapper(ModelMapper m) {
		mapper = m;
	}

	// TODO Refactor - proof of concept
	@Override
	public CompanyInfo getCompanyInfo(String id, String startDate) throws CapIQRequestException {
		try{
			Resource template = new ClassPathResource("META-INF/query/capiq/companyInfo.json");

			String previousDate = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -1);

			Map<String, Object> ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("currentDate", startDate);
			ctx.put("previousDate", previousDate);

			CapIQResponse response = executeCapIqRequests(template, ctx);

			Map<String, String> m = capIqResponseToMap(response);

			CompanyInfo info = (CompanyInfo) mapper.map(m, CompanyInfo.class);

			template = new ClassPathResource("META-INF/query/capiq/closePrice.json");

			Date d = info.getPreviousCloseDate();
			String pre = DateUtil.fromDate(d);

			ctx = new HashMap<String, Object>();
			ctx.put("id", id);
			ctx.put("startDate", pre);

			CapIQResponse prevResp = executeCapIqRequests(template, ctx);
			Map<String, String> m2 = capIqResponseToMap(prevResp);

			CompanyInfo inf = (CompanyInfo) mapper.map(m2, CompanyInfo.class);

			info.setPreviousClosePrice(inf.getClosePrice());
			info.setAvgVolumeM3(getThreeMonthAvg(id, startDate));
			info.setPriceHistory(getLastYear(id, startDate));

			return info;
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Double getThreeMonthAvg(String id, String startDate) throws CapIQRequestException, ParseException {

		String threeMonthsAgo = DateUtil.adjustDate(startDate, Calendar.DAY_OF_MONTH, -91);

		List<List<HistoricalValue>> historicalData = loadHistoricaData(id, threeMonthsAgo);
		List<HistoricalValue> volume = historicalData.get(1);

		Double sum = 0.0;

		for(HistoricalValue v : volume)
			sum += v.getValue();

		return avg(sum, volume.size(), 4);
	}

	private Double avg(Double sum, Integer total, int scale) {
		BigDecimal s = new BigDecimal(sum);
		BigDecimal t = new BigDecimal(total);
		BigDecimal avg = s.divide(t, RoundingMode.HALF_UP);
		return avg.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	public List<HistoricalValue> getLastYear(String id, String startDate) throws CapIQRequestException, ParseException {

		String yearAgo = DateUtil.adjustDate(startDate, Calendar.YEAR, -1);

		List<List<HistoricalValue>> historicalData = loadHistoricaData(id, yearAgo);
		return historicalData.get(0);
	}

	@Override
	public List<CompanyFinancial> getCompanyFinancials(String id) throws CapIQRequestException {
		Resource template = new ClassPathResource("META-INF/query/capiq/companyFinancials.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		CapIQResponse response = executeCapIqRequests(template, ctx);
		List<CompanyFinancial> ret = new ArrayList<CompanyFinancial>();

		try{

			Map<String, Map<String, String>> km = capIqResponseToKeyedMap(response);

			Iterator<Entry<String, Map<String, String>>> i = km.entrySet().iterator();

			while(i.hasNext()){
				Entry<String, Map<String, String>> entry = i.next();
				CompanyFinancial financial = (CompanyFinancial) mapper.map(entry.getValue(), CompanyFinancial.class);

				// AbsPeriod should never be null if there's data
				if(financial != null && financial.getAbsPeriod() != null){
					financial.setTickerCode(id);
					ret.add(financial);
				}
			}

			return ret;

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
	public KeyDevs getKeyDevelopments(String id, String asOfDate) throws CapIQRequestException {
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

	private KeyDev getKeyDev(String id) throws CapIQRequestException {
		Resource template = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		CapIQResponse response = executeCapIqRequests(template, ctx);
		String err = response.getErrorMsg();
		if(StringUtils.isNotEmpty(err)){
			throw new CapIQRequestException("Error response " + err);
		}

		CapIQResult headline = response.getResults().get(0);
		CapIQResult date = response.getResults().get(1);
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

	public List<String> getKeyDevelopmentIds(String id, String asOfDate) throws CapIQRequestException {

		Resource template = new ClassPathResource("META-INF/query/capiq/keyDevs.json");
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", DateUtil.adjustDate(asOfDate, Calendar.MONTH, -1));

		CapIQResponse response = executeCapIqRequests(template, ctx);
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
				if(StringUtils.isNotEmpty(idd)){
					ids.add(idd);
				}
			}
		}

		return ids;
	}

	public Holders getHolderDetails(String id) throws CapIQRequestException {
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

		for(int i = 0; i < names.size(); i++){
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
	public List<List<HistoricalValue>> getHistoricalData(String id, String asOfDate) throws CapIQRequestException {
		String sDate = DateUtil.adjustDate(asOfDate, Calendar.YEAR, -5);
		return loadHistoricaData(id, sDate);
	}

	public List<List<HistoricalValue>> loadHistoricaData(String id, String startDate) throws CapIQRequestException {

		Resource template = new ClassPathResource("META-INF/query/capiq/priceHistory.json");

		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);
		ctx.put("startDate", startDate);

		CapIQResponse response = executeCapIqRequests(template, ctx);

		CapIQResult prices = response.getResults().get(0);
		CapIQResult volumes = response.getResults().get(1);
		List<HistoricalValue> price = getHistory(prices, id);
		List<HistoricalValue> volume = getHistory(volumes, id);

		List ret = new ArrayList();
		ret.add(price);
		ret.add(volume);
		return ret;
	}

	private List<HistoricalValue> getHistory(CapIQResult res, String id) {

		List<HistoricalValue> results = new ArrayList<HistoricalValue>();

		// TODO sanity check headers for index of Date
		for(CapIQRow row : res.getRows()){
			List<String> values = row.getValues();

			String v = values.get(0);
			if(NumberUtils.isNumber(v)){

				HistoricalValue val = new HistoricalValue();
				val.setTickerCode(id);
				val.setValue(Double.valueOf(v));
				val.setDate(DateUtil.toDate(values.get(1)));
				results.add(val);
			}
		}

		return results;
	}

	private CapIQResponse executeCapIqRequests(Resource template, Map<String, Object> ctx) throws CapIQRequestException {
		String query = new CapIQRequest().parseRequest(template, ctx);
		return requestExecutor.execute(query);
	}

	private Map<String, Map<String, String>> capIqResponseToKeyedMap(CapIQResponse response) throws Exception {
		Map<String, Map<String, String>> keyedMap = new HashMap<String, Map<String, String>>();

		for(CapIQResult res : response.getResults()){
			String id = res.getIdentifier();
			String period = res.getProperties().getPeriodType();
			String key = id.concat(period);

			Map<String, String> m = null;

			if(keyedMap.containsKey(key))
				m = keyedMap.get(key);
			else{
				m = new HashMap<String, String>();
				keyedMap.put(key, m);
			}

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
				System.out.println("Duplicate mnenonic found " + header);
				header = header.concat("_prev");
			}

			m.put(header, val);
		}

		return keyedMap;
	}

	private Map<String, String> capIqResponseToMap(CapIQResponse response) throws Exception {
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
}
