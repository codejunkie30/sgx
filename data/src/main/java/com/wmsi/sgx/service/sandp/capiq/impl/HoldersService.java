package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class HoldersService extends AbstractDataService {
	
	@Value("${loader.ownership.dir}")
	private String ownershipDir;
	
	/**
	 * Load Ownership data based on company ticker 
	 * @param company ticker
	 * @return Holders
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	@Override
	public Holders load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		return getHolderDetails(id);
	}
	
	/**
	 * Load Ownership data based on company ticker 
	 * @param company ticker
	 * @return Holders
	 * @throws ResponseParserException
	 * @throws CapIQRequestException
	 */
	public Holders getHolderDetails(String id) throws ResponseParserException, CapIQRequestException {
		Holders hol = new Holders();
		hol.setTickerCode(id);
		Iterable<CSVRecord> records = getCompanyData(id, ownershipDir);
		if (records == null) return hol;
		
		List<Holder> list = new ArrayList<Holder>();
		
		for (CSVRecord record : records) {
			Holder holder = new Holder();
			holder.setName(record.get(2));
			holder.setShares(Long.parseLong((record.get(3).equals(""))?"0":record.get(3)));
			holder.setPercent(Double.parseDouble((record.get(4).equals(""))?"0":record.get(4)));
			list.add(holder);
		}
		
		hol.setHolders(list);

		return hol;
	}
	
}
