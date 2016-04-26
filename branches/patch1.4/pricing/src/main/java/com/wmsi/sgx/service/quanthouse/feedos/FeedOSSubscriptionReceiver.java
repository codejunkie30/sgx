package com.wmsi.sgx.service.quanthouse.feedos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feedos.api.core.Any;
import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.QuotationTradeEventExt;
import com.feedos.api.requests.Receiver_Quotation_ChgSubscribeInstrumentsL1;
import com.feedos.api.requests.Receiver_Quotation_SubscribeInstrumentsL1;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

public class FeedOSSubscriptionReceiver implements Receiver_Quotation_SubscribeInstrumentsL1,
													Receiver_Quotation_ChgSubscribeInstrumentsL1{

	private static final Logger log = LoggerFactory.getLogger(FeedOSSubscriptionReceiver.class);
	
	private Hashtable<Integer, InstrumentQuotationData> instrMap = new Hashtable<Integer, InstrumentQuotationData>();
	private Map<Integer, FeedOSData> feedData;
	
	public FeedOSSubscriptionReceiver(Map<Integer, FeedOSData> data ){
		this.feedData = data;
	}

	private FeedOSSubscriptionObserver observer;
	
	public void setObserver(FeedOSSubscriptionObserver obs) {
		observer = obs;
	}
		
	public void quotSubscribeInstrumentsL1Response(int subscriptionNum, Object userContext, int rc, InstrumentQuotationData[] result) {

		if(rc != Constants.RC_OK){
			log.error("==== Subscription failed, rc=" + PDU.getErrorCodeName(rc));
		}
		else{

			log.error("==== Subscription started");
			//DumpFunctions.dump(result);

			// create new entries in the map, one per instrument received
			for(int i = 0; i < result.length; ++i){

				InstrumentQuotationData quot = result[i];
				PolymorphicInstrumentCode instr = quot.getInstrumentCode();
				int internalCode = instr.get_internal_code();
				
				if(internalCode != 0){

					// create the entry
					instrMap.put(internalCode, quot);
					
					// Update data
					FeedOSData data = feedData.get(internalCode);
					bind(data, quot);
				}
			}
		}
		
		log.error("Item count: {}", feedData.size());
		
		// Notify observers of subscription event.
		observer.subscriptionResponse(new ArrayList<FeedOSData>(feedData.values()));		
	}

	private FeedOSData bind(FeedOSData data, InstrumentQuotationData quot){
		
		data.setLastTradePrice(getDouble(Constants.TAG_LastTradePrice, quot));
		data.setLastPrice(getDouble(Constants.TAG_LastPrice, quot));
		data.setOpenPrice(getDouble(Constants.TAG_DailyOpeningPrice, quot));
		data.setClosePrice(getDouble(Constants.TAG_PreviousClosingPrice, quot));		
		data.setTotalVolume(getDouble(Constants.TAG_DailyTotalVolumeTraded, quot));
		data.setLastTradeVolume(getDouble(Constants.TAG_LastTradeQty, quot));
		data.setHighPrice(getDouble(Constants.TAG_DailyHighPrice, quot));
		data.setLowPrice(getDouble(Constants.TAG_DailyLowPrice, quot));
		
		try{
			data.setCurrentBusinessDay(toIsoDate(Constants.TAG_CurrentBusinessDay, quot));
			data.setPreviousBusinessDay(toIsoDate(Constants.TAG_PreviousBusinessDay, quot));
			data.setLastTradeTimestamp(toIsoDate(Constants.TAG_LastTradeTimestamp, quot));
			data.setLastOffBookTradeTimestamp(toIsoDate(Constants.TAG_LastOffBookTradeTimestamp, quot));
		}
		catch(QuanthouseServiceException e){
			log.error("Could no parse date for quote {}", quot);
		}

		return data;
	}
	
	public void quotNotifTradeEventExt(int subscription_num, Object ctx, int instrumentCode,
										long serverTimestamp, long marketTimestamp, QuotationTradeEventExt event) {

		instrMap.get(instrumentCode).update_with_TradeEventExt(serverTimestamp, event);
		
		String flags = "";
		
		if(event.content_mask.isSetOCHLdaily()){
			flags += "<daily> ";
		}
		if(event.content_mask.isSetOpen()){
			flags += "OPEN ";
		}
		if(event.content_mask.isSetClose()){
			flags += "CLOSE ";
		}
		if(event.content_mask.isSetHigh()){
			flags += "HIGH ";
		}
		if(event.content_mask.isSetLow()){
			flags += "LOW ";
		}

		// check price/trade update
		if(event.content_mask.isSetLastPrice()){
			
			if(event.content_mask.isSetLastTradeQty()){
			
				if(event.content_mask.isSetOffBookTrade()){
					DumpFunctions.DUMP("OFFTRADE: " + event.price + " x " + event.last_trade_qty);
				}
				else{
					DumpFunctions.DUMP("TRADE: " + event.price + " x " + event.last_trade_qty);
				}
			}
			else{
				DumpFunctions.DUMP("PRICE: " + event.price);
			}
			
			InstrumentQuotationData quot = instrMap.get(instrumentCode);

			
			// Update data
			FeedOSData data = feedData.get(instrumentCode);
			bind(data, quot);
			
			data.setAsk(event.best_ask_price);
			data.setBid(event.best_bid_price);
			
			observer.tradeEvent(data);
		}
		else if(flags.length() != 0){
			DumpFunctions.DUMP("OCHL value: " + event.price);			
		}
	}
	
	public void declareEndOfSubscription(int subscription_num, Object user_context, int rc) {
		quotSubscribeInstrumentsL1UnsubNotif(subscription_num, user_context, rc);
	}

	public void quotSubscribeInstrumentsL1UnsubNotif(int subscription_num, Object user_context, int rc){}

	public void quotChgSubscribeInstrumentsAddInstrumentsL1Response(Object userContext, int rc,
			InstrumentQuotationData[] result) {
		log.error("Change Instruments called {}", result.length > 0 ? result[0].getInstrumentCode() : "");

	}

	public void quotChgSubscribeInstrumentsAddOtherValuesToLookForL1Response(Object userContext, int rc,
			InstrumentQuotationData[] result) {
	}

	public void quotChgSubscribeInstrumentsNewContentMaskL1Response(Object userContext, int rc,
			InstrumentQuotationData[] result) {
	}

	public void quotChgSubscribeInstrumentsRemoveInstrumentsL1Response(Object userContext, int rc) {
	}
	
	private Double getDouble(int tag, InstrumentQuotationData quot){
		Double ret = 0.0;
		Any val = quot.getTagByNumber(tag);
		
		if(val != null)
			ret = val.get_float64();
		
		return ret;
	}

	/**
	 * Convert feedOS timestamps to ISO Date that Java can read 
	 */
	private Date toIsoDate(int tag, InstrumentQuotationData quot) throws QuanthouseServiceException{
		SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date ret = null;
		
		try{
			Any val = quot.getTagByNumber(tag);
			
			if(val != null)
				ret = isoDate.parse(PDU.time2ISOstring(val.get_timestamp()));
		}
		catch(ParseException e){
			throw new QuanthouseServiceException("Error parsing dates from api", e);			
		}

		return ret;
	}
	
}
