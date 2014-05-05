package com.wmsi.sgx.service.sandp.capiq.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.Holder;
import com.wmsi.sgx.model.Holders;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.model.sandp.capiq.CapIQRow;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequest;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestExecutor;

@Service
public class HoldersService{

	@Autowired
	private CapIQRequestExecutor requestExecutor;

	public CapIQRequest holdersRequest(){
		return new CapIQRequest(new ClassPathResource("META-INF/query/capiq/holderDetails.json"));		
	}
	
	public Holders getHolders(String id) throws CapIQRequestException {
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.put("id", id);

		CapIQResponse response = requestExecutor.execute(holdersRequest(), ctx);
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
}
