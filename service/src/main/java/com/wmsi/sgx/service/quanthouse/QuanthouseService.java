package com.wmsi.sgx.service.quanthouse;

import com.wmsi.sgx.model.Price;
import com.wmsi.sgx.service.quanthouse.feedos.FeedOSService;

public interface QuanthouseService {

	Price getPrice(String market, String id)throws QuanthouseServiceException;
	void setFeedOSService(FeedOSService s);
}
