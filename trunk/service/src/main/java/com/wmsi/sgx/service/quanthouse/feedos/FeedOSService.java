package com.wmsi.sgx.service.quanthouse.feedos;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	public FeedOSData getPriceData(String market, String id) throws QuanthouseServiceException {
		PolymorphicInstrumentCode instr = getInstrumentCode(market, id);
		return loadFeedOSData(instr);
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
}
