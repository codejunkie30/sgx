package com.wmsi.sgx.service.vwap.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.wmsi.sgx.model.VolWeightedAvgPrice;
import com.wmsi.sgx.model.VolWeightedAvgPrices;
import com.wmsi.sgx.model.VwapAdjustmentFactor;
import com.wmsi.sgx.service.vwap.VWAPServiceException;
import com.wmsi.sgx.service.vwap.VwapService;
import com.wmsi.sgx.util.DateUtil;

import au.com.bytecode.opencsv.CSVReader;

public class VwapServiceImpl implements VwapService {
	private static final Logger log = LoggerFactory.getLogger(VwapServiceImpl.class);
	
	@Value("${loader.adjustment-factor.dir}")
	private String adjustmentFactorDir;

	private Resource vwapData;

	public Resource getVwapData() {
		return vwapData;
	};

	public void setVwapData(Resource d) {
		vwapData = d;
	}

	private Map<String, List<VwapAdjustmentFactor>> adjustmentFactors;

	private VolWeightedAvgPrices vwaps;
	private final String fmt = "dd/MM/yyyy";
	private final String adjFmt = "MM/dd/yyyy";

	@Value("${loader.companies.dir}")
	private String companiesDir = "/mnt/data/companies/";
	
	/**
	 * Get VolWeightedAvgPrices data based on company ticker 
	 * @param company ticker
	 * @return VolWeightedAvgPrices
	 */
	@Override
	public VolWeightedAvgPrices getForTicker(String ticker) {

		List<VolWeightedAvgPrice> vwap = new ArrayList<VolWeightedAvgPrice>();

		for (VolWeightedAvgPrice v : vwaps.getVwaps()) {
			if (v.getTickerCode().equalsIgnoreCase(ticker))
				vwap.add(v);
		}
		VolWeightedAvgPrices ret = new VolWeightedAvgPrices();
		ret.setVwaps(vwap);
		sortVwap(ret);

		return ret;
	}
	/**
	 * Init method for parsing VWAP data
	 * @param 
	 * @return Boolean
	 * @throws VWAPServiceException
	 */
	public Boolean init() throws VWAPServiceException {
		log.info("Reading data from VWAP file...");

		CSVReader csvReader = null;
		InputStreamReader reader = null;
		adjustmentFactors = new HashMap<>();
		try {
			reader = new InputStreamReader(vwapData.getInputStream());
			csvReader = new CSVReader(reader, ',');
			csvReader.readNext();

			String[] record = null;
			List<VolWeightedAvgPrice> ret = new ArrayList<VolWeightedAvgPrice>();

			while ((record = csvReader.readNext()) != null) {
				VolWeightedAvgPrice vap = getRec(record);
				if (vap != null) {
					if (!adjustmentFactors.containsKey(vap.getTickerCode())) {
						adjustmentFactors.put(vap.getTickerCode(), loadAdjustmentFactor(vap.getTickerCode(), "-Catalist.csv"));
					}
					List<VwapAdjustmentFactor> vwapAdjustmentFactors = adjustmentFactors.get(vap.getTickerCode());
					for (VwapAdjustmentFactor vwapAdjustmentFactor : vwapAdjustmentFactors) {
						if (DateUtils.isSameDay(vwapAdjustmentFactor.getPricingDate(), vap.getDate())) {
							vap.setAdjustmentFactorValue(vwapAdjustmentFactor.getWmsiAPI());
							break;
						} 							
					}
					if(vap.getAdjustmentFactorValue() == null){
						vap.setAdjustmentFactorValue("0");
					}
					ret.add(vap);
				}
			}

			log.info("Loaded {} VWAP records", ret.size());
			vwaps = new VolWeightedAvgPrices();
			vwaps.setVwaps(ret);
		} catch (

		IOException e)

		{
			throw new VWAPServiceException("Error parsing VWAP file", e);
		} finally

		{
			IOUtils.closeQuietly(csvReader);
			IOUtils.closeQuietly(reader);
		}

		return true;

	}
	/**
	 * Load list of VwapAdjustmentFactor data based on company ticker 
	 * @param company ticker
	 * @return list of VwapAdjustmentFactor
	 */
	private List<VwapAdjustmentFactor> loadAdjustmentFactor(String stockSymbol, String extension) {
		List<VwapAdjustmentFactor> vwapAdjustmentFactors = new ArrayList<>();
		File f = new File(companiesDir + adjustmentFactorDir+"/" + stockSymbol + extension);
		CSVReader csvReader = null;
		try {
			csvReader = new CSVReader(new FileReader(f), ',');

			String[] record = null;
			while ((record = csvReader.readNext()) != null) {
				VwapAdjustmentFactor vwapAdjustmentFactor = new VwapAdjustmentFactor();
				vwapAdjustmentFactor.setTickerSymbol(record[0].trim());
				vwapAdjustmentFactor.setExchangeSymbol(record[1].trim());
				vwapAdjustmentFactor.setWmsiAPI(record[2].trim());
				vwapAdjustmentFactor.setMaxVolume(record[3].trim());
				vwapAdjustmentFactor.setPricingDate(DateUtil.toDate(record[5], adjFmt));
				vwapAdjustmentFactors.add(vwapAdjustmentFactor);
			}
		} catch (IOException e) {
			if("-Catalist.csv".equals(extension))
				return loadAdjustmentFactor(stockSymbol, "-SGX.csv");
			else
				return vwapAdjustmentFactors;
		} finally {
			IOUtils.closeQuietly(csvReader);
		}
		return vwapAdjustmentFactors;
	}
	
	/**
	 * Load VolWeightedAvgPrice data based on list of InputRecords
	 * @param list of InputRecords
	 * @return VolWeightedAvgPrice
	 */
	private VolWeightedAvgPrice getRec(String[] record) {
		try {
			VolWeightedAvgPrice r = new VolWeightedAvgPrice();

			r.setExchange(record[0].trim());
			r.setTickerCode(record[1].trim());
			r.setDate(DateUtil.toDate(record[2], fmt));
			r.setValue(record[3].trim());
			r.setVolume(record[4].trim());
			r.setCurrency(record[5].trim());
			return r;
		} catch (Exception e) {
			log.error("VWAP BAD ROW", record);
		}
		return null;
	}

	private void sortVwap(VolWeightedAvgPrices vwap) {

		List<VolWeightedAvgPrice> indexes = vwap.getVwaps();

		// Sort desc by date
		Collections.sort(indexes, new Comparator<VolWeightedAvgPrice>() {

			@Override
			public int compare(VolWeightedAvgPrice o1, VolWeightedAvgPrice o2) {
				return o2.getDate().compareTo(o1.getDate());

			}

		});

	}

}