package com.wmsi.sgx.service.quanthouse.feedos;

import java.util.List;

public interface FeedOSSubscriptionObserver{

	void subscriptionResponse(List<FeedOSData> data);
	void tradeEvent(FeedOSData data);

}
