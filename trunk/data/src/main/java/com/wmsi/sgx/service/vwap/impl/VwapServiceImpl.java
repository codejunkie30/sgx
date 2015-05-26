package com.wmsi.sgx.service.vwap.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.model.VolWeightedAvgPrice;
import com.wmsi.sgx.model.VolWeightedAvgPrices;
import com.wmsi.sgx.service.vwap.VWAPServiceException;
import com.wmsi.sgx.service.vwap.VwapService;
import com.wmsi.sgx.util.DateUtil;

public class VwapServiceImpl implements VwapService{
	private static final Logger log = LoggerFactory.getLogger(VwapServiceImpl.class);
	
	private Resource vwapData;
	public Resource getVwapData(){return vwapData;};
	public void setVwapData(Resource d){vwapData = d;}
	
	private VolWeightedAvgPrices vwaps;
	private final String fmt = "dd/MM/yyyy";
	
	@Override
	public VolWeightedAvgPrices getForTicker(String ticker){
		
		List<VolWeightedAvgPrice> vwap = new ArrayList<VolWeightedAvgPrice>();
		
		for(VolWeightedAvgPrice v : vwaps.getVwaps()){
			if(v.getTickerCode().equalsIgnoreCase(ticker))
				vwap.add(v);
		}
		VolWeightedAvgPrices ret = new VolWeightedAvgPrices();
		ret.setVwaps(vwap);
		sortVwap(ret);
		
		return ret;
	}
	
	
	public Boolean init() throws VWAPServiceException{
		log.info("Reading data from VWAP file...");
		
		CSVReader csvReader = null;
		InputStreamReader reader = null;
		
		try{
			reader = new InputStreamReader(vwapData.getInputStream());
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext();
			
			String[] record = null;
			List<VolWeightedAvgPrice> ret = new ArrayList<VolWeightedAvgPrice>();
			
			while((record = csvReader.readNext()) != null){
				ret.add(getRec(record));
				
			}
			
			log.info("Loaded {} VWAP records", ret.size());
			vwaps = new VolWeightedAvgPrices();
			vwaps.setVwaps(ret);
		}
		catch(IOException e){
			throw new VWAPServiceException("Error parsing VWAP file", e);
		}
		finally{
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
		
		return true;
	}
	
	public VolWeightedAvgPrice getRec(String[] record){
		
		VolWeightedAvgPrice r = new VolWeightedAvgPrice();
		
		r.setExchange(record[0].trim());
		r.setTickerCode(record[1].trim());
		r.setDate(DateUtil.toDate(record[2], fmt));
		r.setValue(record[3].trim());
		r.setVolume(record[4].trim());				
		r.setCurrency(record[5].trim());
		return r;
		
	}
	
	private void sortVwap(VolWeightedAvgPrices vwap){
		
		List<VolWeightedAvgPrice> indexes = vwap.getVwaps();
		
		// Sort desc by date
		Collections.sort(indexes, new Comparator<VolWeightedAvgPrice>(){

			@Override
			public int compare(VolWeightedAvgPrice o1, VolWeightedAvgPrice o2){				
				return o2.getDate().compareTo(o1.getDate());
				
			}
			
		});

	}
	
}