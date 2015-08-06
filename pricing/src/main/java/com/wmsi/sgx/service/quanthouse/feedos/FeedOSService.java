package com.wmsi.sgx.service.quanthouse.feedos;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedos.api.core.Any;
import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.core.Session;
import com.feedos.api.requests.Constants;
import com.feedos.api.requests.InstrumentCharacteristics;
import com.feedos.api.requests.InstrumentQuotationData;
import com.feedos.api.requests.QuotationContentMask;
import com.feedos.api.requests.Receiver_Quotation_GetHistoryIntraday2;
import com.feedos.api.requests.RequestSender;
import com.feedos.api.requests.SyncRequestSender;
import com.feedos.api.tools.Verbosity;
import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Service
public class FeedOSService {
	
	private Logger log = LoggerFactory.getLogger(FeedOSService.class);

	@Autowired
	private FeedOSSession session;
	public void setFeedOSSession(FeedOSSession s){session = s;}
	
	private Integer subscriptionNumber;	
	
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
		
		// Collect basic trading data
		for(InstrumentCharacteristics chara : referenceInstruments){
			
			// Get trading currency from ref instruments
			Any curr = chara.getRef_values().getTagByNumber(Constants.TAG_PriceCurrency);
			Any ticker = chara.getRef_values().getTagByNumber(Constants.TAG_Symbol);
			Any isin = chara.getRef_values().getTagByNumber(Constants.TAG_ISIN);
			
			if(chara.getInternal_instrument_code() == 0){
				log.error("Invalid instrument code");
				continue;
			}
			
			log.error("symbol: {}, ISIN: {}", curr.get_string(), isin.get_string());			
			
			FeedOSData fosd = new FeedOSData();
			fosd.setTradingCurrency(curr.get_string());
			fosd.setTradingSymbol(ticker.get_string());
			fosd.setIsin(isin.get_string());
			fosd.setId(chara.getInternal_instrument_code());
			data.put(chara.getInternal_instrument_code(), fosd);
		}
		
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
	
	private FeedOSData loadFeedOSData(PolymorphicInstrumentCode instr) throws QuanthouseServiceException{
		
		FeedOSData feedOSData = null;
		
		Session ses = session.getSession();		
		SyncRequestSender sender = new SyncRequestSender(ses,0);
		
		try{
			PolymorphicInstrumentCode[] instrs = new PolymorphicInstrumentCode[]{instr};
			
			InstrumentQuotationData quote = getSnapshotInstrumentsL1(sender, instrs);
			InstrumentCharacteristics chara = getReferenceInstruments(sender, instrs);
			
			// Bind to pojo
			feedOSData = bind(quote, chara);			
		}
		catch(FeedOSException e) {

			if(e.rc == Constants.RC_INVALID_INSTRUMENT_CODE){
				String code = MessageFormat.format("{0}@{1}", instr.get_market_id(), instr.get_local_code_str());
				log.error("Invalid instrument code. Code: {} Internal: {}", code, instr.get_internal_code());
				throw new InvalidInstrumentException("Invalid instrument code " + code);
			}
			
			throw new QuanthouseServiceException("Error retrieving last price", e);
		}

		return feedOSData;
	}
	
	private InstrumentQuotationData getSnapshotInstrumentsL1(SyncRequestSender sender, PolymorphicInstrumentCode[] instrs) throws QuanthouseServiceException, FeedOSException{
		
		InstrumentQuotationData snapshot = null;		
		InstrumentQuotationData[] quote = sender.syncQuotSnapshotInstrumentsL1(instrs, null);

		if(quote != null){
			
			if(quote.length != instrs.length)
				throw new QuanthouseServiceException("FeedOS results size mismatch, expected " + instrs.length + " results got " + quote.length);
			
			snapshot = quote[0];
		}
		
		return snapshot;
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

	private InstrumentCharacteristics getReferenceInstruments(SyncRequestSender sender, PolymorphicInstrumentCode[] instrs) throws QuanthouseServiceException, FeedOSException{

		// Get reference instrument for meta data about market and id
		InstrumentCharacteristics chara = null;		
		InstrumentCharacteristics[] refInstr = sender.syncRefGetInstruments(instrs, null);

		if(refInstr != null){
			
			if(refInstr.length != instrs.length)
				throw new QuanthouseServiceException("FeedOS results size mismatch, expected " + instrs.length + " results got " + refInstr.length);
			
			chara = refInstr[0];
		}
		
		return chara;
	}

	/**
	 * Convert market and id to FeedOS internal instrument code
	 */
	private PolymorphicInstrumentCode getInstrumentCode(String market, String id){
		Verbosity.enableVerbosity(); // Has to be initialized or GetFOSMarketID will fail quietly
		int marketId = Verbosity.getFOSMarketId(market);
		return new PolymorphicInstrumentCode(marketId, id);
	}

	private FeedOSData bind(InstrumentQuotationData quot, InstrumentCharacteristics chara ) throws QuanthouseServiceException{
		
		FeedOSData data = new FeedOSData();		
		data.setLastPrice(getDouble(Constants.TAG_LastPrice, quot));
		data.setOpenPrice(getDouble(Constants.TAG_DailyOpeningPrice, quot));
		data.setClosePrice(getDouble(Constants.TAG_PreviousClosingPrice, quot));
		data.setCurrentBusinessDay(toIsoDate(Constants.TAG_CurrentBusinessDay, quot ));
		data.setPreviousBusinessDay(toIsoDate(Constants.TAG_PreviousBusinessDay, quot ));
		data.setLastTradeTimestamp(toIsoDate(Constants.TAG_LastTradeTimestamp, quot));
		data.setLastOffBookTradeTimestamp(toIsoDate(Constants.TAG_LastOffBookTradeTimestamp, quot));
		
		// Get trading currency from ref instruments
		Any curr = chara.getRef_values().getTagByNumber(Constants.TAG_PriceCurrency);
		data.setTradingCurrency(curr.get_string());
		
		return data;
	}
	
	private Double getDouble(int tag, InstrumentQuotationData quot){
		Double ret = null;
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

	private Date toIsoDate(long time) throws QuanthouseServiceException{
		SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date ret = null;
		
		try{
			ret = isoDate.parse(PDU.time2ISOstring(time));
		}
		catch(ParseException e){
			throw new QuanthouseServiceException("Error parsing dates from api", e);			
		}

		return ret;
	}

/*
	class MySubscribeInstrumentsReceiverL1 implements Receiver_Quotation_SubscribeInstrumentsL1,
			Receiver_Quotation_ChgSubscribeInstrumentsL1{
		// let's store "instrument status" values, indexed by internal_code
		Hashtable<Integer, InstrumentQuotationData> instrMap = new Hashtable<Integer, InstrumentQuotationData>();

		PolymorphicInstrumentCode[] input_instr_codes;

		// pass the list of "polymorphic codes" that this receiver is supposed
		// to handle.
		// This will be used to "enrich" instrument codes received.
		MySubscribeInstrumentsReceiverL1(PolymorphicInstrumentCode[] the_instr_codes){
			input_instr_codes = the_instr_codes;
		}

		public void quotSubscribeInstrumentsL1Response(int subscription_num, Object user_context, int rc,
				InstrumentQuotationData[] result) {
			
			if(rc != Constants.RC_OK){
				log.error("==== Subscription failed, rc=" + PDU.getErrorCodeName(rc));
			}
			else{
			
				log.error("==== Subscription started");
				log.error(result.toString());

				DumpFunctions.dump(result);
				
				// create new entries in the map, one per instrument received
				for(int i = 0; i < result.length; ++i){
					
					PolymorphicInstrumentCode instr = result[i].getInstrumentCode();

					// int internal_instr_code =
					// instr.instrument_code.get_internal_code();
					int internal_instr_code = instr.get_internal_code();
					
					log.error("Inst code " + internal_instr_code);
					if(0 == internal_instr_code){
						// this may happen:
						// 1) an invalid instr code was provided in request
						// 2) IgnoreInvalidCodes was set to true (hence the
						// request succeded despite the invalid input)
					}
					else{

						// enrich the instrument code because:
						// 1) only the "local code" flavour was set in the
						// request
						// 2) only the "internal" flavour is present in the
						// response data
						instr.merge_local_code(input_instr_codes[i].get_local_code_str());
						input_instr_codes[i].merge_internal_code(instr.get_internal_code());
						
						// create the entry
						instrMap.put(new Integer(internal_instr_code), result[i]);
					}
				}
			}
		}

		public void declareEndOfSubscription(int subscription_num, Object user_context, int rc) {
			quotSubscribeInstrumentsL1UnsubNotif(subscription_num, user_context, rc);
		}

		public void quotSubscribeInstrumentsL1UnsubNotif(int subscription_num, Object user_context, int rc) {
			/*
			DumpFunctions.DUMP("==== Subscription ended: " + input_instr_codes.length);

			for(int i = 0; i < input_instr_codes.length; ++i){
				DumpFunctions.dump(instrMap.get(input_instr_codes[i].get_internal_code()));
			}
		}

		public void quotNotifTradeEventExt(int subscription_num, Object user_context, int instrument_code,
				long server_timestamp, long market_timestamp, QuotationTradeEventExt trade_event) {

			instrMap.get(instrument_code).update_with_TradeEventExt(server_timestamp, trade_event);

			InstrumentQuotationData quot = instrMap.get(instrument_code);
			FeedOSData data = new FeedOSData();		
			data.setLastPrice(getDouble(Constants.TAG_LastPrice, quot));
			data.setOpenPrice(getDouble(Constants.TAG_DailyOpeningPrice, quot));
			data.setClosePrice(getDouble(Constants.TAG_PreviousClosingPrice, quot));
			data.setVolume(getDouble(Constants.TAG_DailyTotalVolumeTraded, quot));
			
			try{
				data.setCurrentBusinessDay(toIsoDate(Constants.TAG_CurrentBusinessDay, quot ));
				data.setPreviousBusinessDay(toIsoDate(Constants.TAG_PreviousBusinessDay, quot ));
				data.setLastTradeTimestamp(toIsoDate(Constants.TAG_LastTradeTimestamp, quot));
				data.setLastOffBookTradeTimestamp(toIsoDate(Constants.TAG_LastOffBookTradeTimestamp, quot));				
			}
			catch(QuanthouseServiceException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			log.error("Data: {}", data);
			String market_timestamp_str = PDU.date2ISOstring(market_timestamp);
			String server_timestamp_str = PDU.date2ISOstring(server_timestamp);

			/*
			log.error("Trade event {} {} {} {}",  market_timestamp_str, 
					instrument_code, 
					trade_event.price, 
					trade_event.last_trade_qty,
					trade_event.getValue()
					);

			DumpFunctions.DUMP("==== " + server_timestamp_str + "\t" + market_timestamp_str);
			++DumpFunctions.indent_count;
			*
			//
			// now parse the event and dump values
			//
			// check best limits
			if(trade_event.content_mask.isSetBidLimit()){
				DumpFunctions.DUMP("BEST BID: " + trade_event.best_bid_price + " x " + trade_event.best_bid_qty);
			}
			if(trade_event.content_mask.isSetAskLimit()){
				DumpFunctions.DUMP("BEST ASK: " + trade_event.best_ask_price + " x " + trade_event.best_ask_qty);
			}

			//log.error("content mask: {}", trade_event.content_mask.printContent());
			
			String flags = "";
			if(trade_event.content_mask.isSetOCHLdaily()){
				flags += "<daily> ";
			}
			if(trade_event.content_mask.isSetOpen()){
				flags += "OPEN ";
			}
			if(trade_event.content_mask.isSetClose()){
				flags += "CLOSE ";
			}
			if(trade_event.content_mask.isSetHigh()){
				flags += "HIGH ";
			}
			if(trade_event.content_mask.isSetLow()){
				flags += "LOW ";
			}

			if(flags.length() != 0){
				DumpFunctions.DUMP("flags: " + flags);
			}

			// check price/trade update
			if(trade_event.content_mask.isSetLastPrice()){
				if(trade_event.content_mask.isSetLastTradeQty()){
					if(trade_event.content_mask.isSetOffBookTrade()){
						DumpFunctions.DUMP("OFFTRADE: " + trade_event.price + " x " + trade_event.last_trade_qty);
					}
					else{
						DumpFunctions.DUMP("TRADE: " + trade_event.price + " x " + trade_event.last_trade_qty);
					}
				}
				else{
					DumpFunctions.DUMP("PRICE: " + trade_event.price);
				}
			}
			else{
				if(flags.length() != 0){
					DumpFunctions.DUMP("OCHL value: " + trade_event.price);
				}
			}

			if(trade_event.getContext().size() > 0)
				DumpFunctions.DUMP("Context: ");
			DumpFunctions.dump(trade_event.getContext());

			if(trade_event.getValue().size() > 0)
				DumpFunctions.DUMP("Values: ");
			DumpFunctions.dump(trade_event.getValue());

			--DumpFunctions.indent_count;
			
		}

		public void quotChgSubscribeInstrumentsAddInstrumentsL1Response(Object userContext, int rc,
				InstrumentQuotationData[] result) {
			log.error("Change Instruments called {}", result.length > 0 ? result[0].getInstrumentCode() : "");

		}

		public void quotChgSubscribeInstrumentsAddOtherValuesToLookForL1Response(Object userContext, int rc,
				InstrumentQuotationData[] result) {
			// TODO Auto-generated method stub

		}

		public void quotChgSubscribeInstrumentsNewContentMaskL1Response(Object userContext, int rc,
				InstrumentQuotationData[] result) {
			// TODO Auto-generated method stub

		}

		public void quotChgSubscribeInstrumentsRemoveInstrumentsL1Response(Object userContext, int rc) {
			// TODO Auto-generated method stub

		}
	}
	*/
	
}
