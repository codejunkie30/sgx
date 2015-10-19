package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;

@SuppressWarnings("unchecked")
public class HoldersService extends AbstractDataService {

	
	@Override
	public Holders load(String id, String... parms) throws CapIQRequestException, ResponseParserException {
		/*Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		return executeRequest(new CapIQRequestImpl(template), ctx);*/
		id=id.split(":")[0];
		return getHolderDetails(id);
	}
	
	public Holders getHolderDetails(String id) throws ResponseParserException, CapIQRequestException {
		Holders hol = new Holders();
		hol.setTickerCode(id);
		
		String file = "src/main/resources/data/ownership.csv";
		CSVHelperUtil csvHelperUtil = new CSVHelperUtil();
		Iterable<CSVRecord> records = csvHelperUtil.getRecords(file);
		List<Holder> list = new ArrayList<Holder>();
		
		for (CSVRecord record : records) {
			Holder holder = new Holder();
			holder.setName(record.get(2));
			holder.setShares(Long.parseLong((record.get(3).equals(""))?"0":record.get(3)));
			holder.setPercent(Double.parseDouble((record.get(4).equals(""))?"0":record.get(4)));
			
			list.add(holder);
		}
		
		hol.setHolders(list);
		System.out.println(hol);
		return hol;
	}
	
}
