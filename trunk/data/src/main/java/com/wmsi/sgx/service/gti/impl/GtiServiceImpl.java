package com.wmsi.sgx.service.gti.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.GovTransparencyIndex;
import com.wmsi.sgx.model.GovTransparencyIndexes;
import com.wmsi.sgx.service.gti.GTIServiceException;
import com.wmsi.sgx.service.gti.GtiService;

/**
 * Service for loading Government Transparency Index data. This service loads this 
 * data from a flat file and store it in memory. 
 * @author JLee
 *
 */
public class GtiServiceImpl implements GtiService{

	private static final Logger log = LoggerFactory.getLogger(GtiServiceImpl.class);
	
	private Resource gtiData;	
	public Resource getGtiData(){return gtiData;}
	public void setGtiData(Resource d){gtiData = d;}

	private GovTransparencyIndexes gtis;
	
	/**
	 * Get GTI data for latest year for the given ticker.
	 * @param ticker The ticker to load GTIs for.
	 */
	@Override
	public GovTransparencyIndex getLatest(String ticker){
		GovTransparencyIndexes indexes = getForTicker(ticker);
		
		if(indexes == null || indexes.getGtis().size() <= 0)
			return null;
		
		// Sort desc and return first item. 
		sortGti(indexes);		
		
		return indexes.getGtis().get(0);
	}

	
	/**
	 * Get all GTIs for the given ticker
	 * @param ticker The ticker to load GTIs for.
	 */
	@Override
	public GovTransparencyIndexes getForTicker(String ticker){
		
		List<GovTransparencyIndex> gits = new ArrayList<GovTransparencyIndex>();
		
		for(GovTransparencyIndex i : gtis.getGtis()){
			if(i.getTicker().equalsIgnoreCase(ticker))
				gits.add(i);
		}
		
		GovTransparencyIndexes ret = new GovTransparencyIndexes();
		ret.setGtis(gits);
		
		return ret;
	}
	
	
	/**
	 * Load all GTI data into memory. The data comes in an excel
	 * file but is exported to a headerless csv file for simplicity.
	 *
	 * @throws GTIServiceException
	 */
	public Boolean init() throws GTIServiceException {

		log.info("Reading data from GTI file...");

		CSVReader csvReader = null;
		InputStreamReader reader = null;

		try{
			DateFormat df = new SimpleDateFormat("dd-MMM-yy");
			
			reader = new InputStreamReader(gtiData.getInputStream());
			csvReader = new CSVReader(reader, ',');
			//csvReader.readNext(); // skip header

			String[] record = null;
			List<GovTransparencyIndex> ret = new ArrayList<GovTransparencyIndex>();

			while((record = csvReader.readNext()) != null){

				GovTransparencyIndex r = new GovTransparencyIndex();
				r.setRank(Integer.valueOf(record[0].trim()));
				r.setIssue(record[1].trim());
				r.setIsin(record[2].trim());
				r.setTicker(record[3].trim());
				r.setCompanyName(record[4].trim());
				
				try{
					r.setDate(df.parse(record[5]));
				}
				catch(ParseException e){
					// Skip rows with bad dates. 
					log.warn("Error parsing GTI date for ticker {}", r.getTicker());
					continue;
				}
				
				r.setBaseScore(Integer.valueOf(record[6]));
				r.setAdjustment(Integer.valueOf(record[7]));
				r.setTotalScore(Integer.valueOf(record[8]));
				ret.add(r);
			}

			log.info("Loaded {} GTI records", ret.size());

			gtis = new GovTransparencyIndexes();
			gtis.setGtis(ret);
			
			// Calculate rank changes manually
			calculateChanges(gtis);

		}
		catch(IOException e){
			throw new GTIServiceException("Error parsing GTI file", e);
		}
		finally{
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}
		
		return true;
	}

	private List<String> getAllTickers(GovTransparencyIndexes gtis){
		List<String> tickers = new ArrayList<String>();
		
		for(GovTransparencyIndex i : gtis.getGtis()){
			
			if(!tickers.contains(i.getTicker()))
					tickers.add(i.getTicker());
		}
		
		return tickers;
	}
	
	/**
	 * Calculate the changes in Rank for all tickers and all dates. 
	 * @param gtis
	 */
	private void calculateChanges(GovTransparencyIndexes gtis){
		
		for(String ticker : getAllTickers(gtis)){
			
			GovTransparencyIndexes gti = getForTicker(ticker);
			
			// Sort by date desc
			sortGti(gti);
			
			List<GovTransparencyIndex> indexes = gti.getGtis();
			
			for(int i=0; i < indexes.size() -1; i++){
				GovTransparencyIndex current = indexes.get(i);
				GovTransparencyIndex previous = indexes.get(i + 1);
				current.setRankChange(previous.getRank() - current.getRank());
			}
		}
	}
	
	private void sortGti(GovTransparencyIndexes gtis){
		
		List<GovTransparencyIndex> indexes = gtis.getGtis();
		
		// Sort desc by date
		Collections.sort(indexes, new Comparator<GovTransparencyIndex>(){

			@Override
			public int compare(GovTransparencyIndex o1, GovTransparencyIndex o2){				
				return o2.getDate().compareTo(o1.getDate());
				
			}
			
		});

	}
}
