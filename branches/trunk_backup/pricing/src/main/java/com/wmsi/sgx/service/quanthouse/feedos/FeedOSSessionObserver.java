package com.wmsi.sgx.service.quanthouse.feedos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feedos.api.core.Session;
import com.feedos.api.core.SessionObserver;

public class FeedOSSessionObserver implements SessionObserver{

	private Logger log = LoggerFactory.getLogger(FeedOSSessionObserver.class);
	
	public void adminMessage(Session arg0, boolean arg1, String arg2, String arg3, String arg4) {
		log.debug("Session Admin Message: {} {} {} {}", arg1, arg2, arg3, arg4);
	}

	public void closeComplete(Session arg0){
		log.info("Session closed");		
	}

	public void closeInProgress(Session arg0){
		log.info("Session close in progress...");
	}

	public void heartbeat(Session arg0, long arg1){
		log.info("Session heartbeat {}", arg1);		
	}

	public void sessionOpened(Session arg0, int arg1, int arg2, int arg3) {
		log.info("Session opened. Args: {} {} {}", arg1, arg2, arg3);	
	}
}
