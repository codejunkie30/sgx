package com.wmsi.sgx.service.quanthouse.feedos;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.feedos.api.core.FeedOSException;
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
	
	/**
	 * Get the last price for the given id within the given market
	 * @param market - Market ID belongs too
	 * @param id - Local Market identifier
	 * @return The last price
	 * @throws QuanthouseServiceException
	 */
	public Double getLastPrice(String market, String id) throws QuanthouseServiceException{
		PolymorphicInstrumentCode instr = getInstrumentCode(market, id);
		InstrumentQuotationData quot = getSnapshotInstrumentsL1(instr);
		return quot.getTagByNumber(Constants.TAG_LastPrice).get_float64();
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

}
