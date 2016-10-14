package com.wmsi.sgx.service.quanthouse.feedos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedos.api.core.Any;
import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.core.Session;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentCharacteristics;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.RequestSender;
import com.feedos.api.requests.SyncRequestSender;
import com.feedos.api.tools.Verbosity;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Service
public class FeedOSService {
	
	private Logger log = LoggerFactory.getLogger(FeedOSService.class);

	@Autowired
	private FeedOSSession session;
	public void setFeedOSSession(FeedOSSession s){session = s;}
	
	private Integer subscriptionNumber;	
	
	/**
	 * Subscrie to QuanthouseService
	 * @param market code
	 * @param List of stock tickers
	 * @param FeedOSSubscriptionObserver
	 * @return 
	 * @throws QuanthouseServiceException
	 */
	public synchronized void subscribe(String market, List<String> ids, FeedOSSubscriptionObserver observer) throws QuanthouseServiceException{
	
		Session ses = session.getSession();
		RequestSender async_requester = new RequestSender (ses, 0);
		async_requester.allow_invalid_instrument_codes();
		
		// Kill current subscriptions if running
		if(subscriptionNumber != null)
			async_requester.asyncQuotSubscribeInstrumentsL1_stop(subscriptionNumber);
		
		Verbosity.enableVerbosity(); // Has to be initialized or GetFOSMarketID will fail quietly
		
		int marketId = Verbosity.getFOSMarketId(market);

		QuotationContentMask requested_content = new QuotationContentMask(true);	// request all events
		PolymorphicInstrumentCode[] instrs = new PolymorphicInstrumentCode[ids.size()];
		
		for(int i=0; i< ids.size(); i++){
			instrs[i] = new PolymorphicInstrumentCode(marketId, ids.get(i));
		}
		
		SyncRequestSender sender = new SyncRequestSender(ses,0);
		sender.allow_invalid_instrument_codes();
		
		InstrumentCharacteristics[] referenceInstruments = getAllReferenceInstruments(sender, instrs);
		
		Map<Integer, FeedOSData> data = new HashMap<Integer, FeedOSData>();
		int nonSGX = 0;
		
		// Collect basic trading data
		for(InstrumentCharacteristics chara : referenceInstruments){
			
			// Get trading currency from ref instruments
			Any curr = chara.getRef_values().getTagByNumber(Constants.TAG_PriceCurrency);
			Any ticker = chara.getRef_values().getTagByNumber(Constants.TAG_Symbol);
			Any isin = chara.getRef_values().getTagByNumber(Constants.TAG_ISIN);
			
			if(chara.getInternal_instrument_code() == 0){
				nonSGX++;
			}else{					
				
				FeedOSData fosd = new FeedOSData();
				fosd.setTradingCurrency(curr.get_string());
				fosd.setTradingSymbol(ticker.get_string());				
				if(isin != null){
					//log.error("symbol: {}, ISIN: {}", curr.get_string(), isin.get_string());
					fosd.setIsin(isin.get_string());
				}
				fosd.setId(chara.getInternal_instrument_code());
				fosd.setMarket(market);
				data.put(chara.getInternal_instrument_code(), fosd);
			}
		}
		
		log.warn("Number of invalid instrument codes: {}", nonSGX);
		
		String userContext = "1";
		
		FeedOSSubscriptionReceiver receiver = new FeedOSSubscriptionReceiver(data);
		receiver.setObserver(observer);
		
		// STORE the returned value: we'll need it to stop the subscription
		subscriptionNumber = async_requester.asyncQuotSubscribeInstrumentsL1_start
			(
					receiver,
					userContext,
					instrs,		// list of instr code
					null,			// other variables to look for (none)
					requested_content
			);

	}
		
	private InstrumentCharacteristics[] getAllReferenceInstruments(SyncRequestSender sender, PolymorphicInstrumentCode[] instrs) throws QuanthouseServiceException{

		try{
			// Get reference instrument for meta data about market and id
			InstrumentCharacteristics[]  refInstr = sender.syncRefGetInstruments(instrs, null);
			
			if(refInstr == null || refInstr.length != instrs.length)
				throw new QuanthouseServiceException("FeedOS results size mismatch, expected " + instrs.length + " results got " + refInstr.length);
			
			return refInstr;		

		}
		catch(FeedOSException e){
			throw new QuanthouseServiceException("Exception fetching ref instruments", e);
		}
	}
	
}
