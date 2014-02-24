package com.wmsi.sgx.service.quanthouse.feedos;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedos.api.core.FeedOSException;
import com.feedos.api.core.PDU;
import com.feedos.api.core.PolymorphicInstrumentCode;
import com.feedos.api.core.Session;
import com.feedos.api.requests.Constants;
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
		InstrumentQuotationData quot = getSnapshotInstrumentsL1(instr);
		return bind(quot);
	}

	private InstrumentQuotationData getSnapshotInstrumentsL1(PolymorphicInstrumentCode instr) throws QuanthouseServiceException{
		
		InstrumentQuotationData snapshot = null;
		
		Session ses = session.open();		
		SyncRequestSender sender = new SyncRequestSender(ses,0);
		
		try{
			PolymorphicInstrumentCode[] instrs = new PolymorphicInstrumentCode[]{instr};
			InstrumentQuotationData[] data = sender.syncQuotSnapshotInstrumentsL1(instrs, null);
			
			if(data != null){
				
				if(data.length != instrs.length)
					throw new QuanthouseServiceException("FeedOS results size mismatch, expected " + instrs.length + " results got " + data.length);
				
				snapshot = data[0];				
			}
		}
		catch(FeedOSException e) {

			if(e.rc == Constants.RC_INVALID_INSTRUMENT_CODE){
				String code = MessageFormat.format("{0}@{1}", instr.get_market_id(), instr.get_local_code_str());
				log.error("Invalid instrument code. Code: {} Internal: {}", code, instr.get_internal_code());
				throw new InvalidInstrumentException("Invalid instrument code " + code);
			}
			
			throw new QuanthouseServiceException("Error retrieving last price", e);
		}
		finally{
			ses.close();
		}

		return snapshot;
	}

	/**
	 * Convert market and id to FeedOS internal instrument code
	 */
	private PolymorphicInstrumentCode getInstrumentCode(String market, String id){
		Verbosity.enableVerbosity(); // Has to be initialized or GetFOSMarketID will fail quietly
		int marketId = Verbosity.getFOSMarketId(market);
		return new PolymorphicInstrumentCode(marketId, id);
	}

	private FeedOSData bind(InstrumentQuotationData quot) throws QuanthouseServiceException{
		FeedOSData data = new FeedOSData();
		
		Double lastPrice = quot.getTagByNumber(Constants.TAG_LastPrice).get_float64();
		Double openPrice = quot.getTagByNumber(Constants.TAG_DailyOpeningPrice).get_float64();
		Double closePrice = quot.getTagByNumber(Constants.TAG_PreviousClosingPrice).get_float64();		
		Long currentDay = quot.getTagByNumber(Constants.TAG_CurrentBusinessDay).get_timestamp();
		Long previousDay = quot.getTagByNumber(Constants.TAG_PreviousBusinessDay).get_timestamp();
		
		data.setLastPrice(lastPrice);
		data.setOpenPrice(openPrice);
		data.setClosePrice(closePrice);
		data.setCurrentBusinessDay(toIsoDate(currentDay));
		data.setPreviousBusinessDay(toIsoDate(previousDay));
		
		return data;
	}
	
	/**
	 * Convert feedOS timestamps to ISO Date that Java can read 
	 */
	private Date toIsoDate(Long timestamp) throws QuanthouseServiceException{
		SimpleDateFormat isoDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		Date ret = null;
		
		try{
			ret = isoDate.parse(PDU.time2ISOstring(timestamp));
		}
		catch(ParseException e){
			throw new QuanthouseServiceException("Error parsing dates from api", e);			
		}

		return ret;
	}
}
