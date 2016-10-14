package com.wmsi.sgx.service.quanthouse.feedos;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.feedos.api.core.Credentials;
import com.feedos.api.core.PDU;
import com.feedos.api.core.ProxyFeedosTCP;
import com.feedos.api.core.Session;
import com.feedos.api.requests.Constants;
import com.feedos.api.tools.Verbosity;
import com.wmsi.sgx.service.quanthouse.QuanthouseServiceException;

@Component
public class FeedOSSession{

	private Logger log = LoggerFactory.getLogger(FeedOSSession.class);
	
	private Session session;
	
	@Autowired
	private FeedOSConfig feedOSConfig;
	public void setFeedOSconfig(FeedOSConfig c){feedOSConfig = c;}
	
	/**
	 * get session for QuanthouseService
	 * @param 
	 * @return Session
	 * @throws QuanthouseServiceException
	 */
	public synchronized Session getSession() throws QuanthouseServiceException{
		if(session == null || !session.isOpened())
			session = open();
		
		return session;
	}
	
	/**
	 * Open session for QuanthouseService
	 * @param 
	 * @return Session
	 * @throws QuanthouseServiceException
	 */
	public synchronized Session open() throws QuanthouseServiceException{

		// Get connection settings from config
		String sessionName = feedOSConfig.getSessionName();
		String url = feedOSConfig.getUrl();
		Integer port = feedOSConfig.getPort();

		log.debug("Initializing session {}", sessionName);
		session = new Session();
		
		if(Session.init_api(sessionName) != 0){			
			throw new QuanthouseServiceException("Failed to create session");
		}

		log.debug("Connecting to FeedOS server: {}:{}", url, port );

		// Needs to be enabled or the session open call below will complain about encoding
		Verbosity.enableVerbosity();
		
		int rc = session.open(new FeedOSSessionObserver(), new ProxyFeedosTCP(url, port, new Credentials(feedOSConfig.getUser(), feedOSConfig.getPassword())), 0);
		
		if (rc != Constants.RC_OK){
			log.error("Connection to FeedOS server: {}:{} returned error code: {}", url, port, PDU.getErrorCodeName(rc));
			throw new QuanthouseServiceException("Cannot connect to feedOS server");			
		}

		log.debug("Connected successfully to server: {}:{}", url, port);
		
		return session;
	}

	@PreDestroy
	public synchronized void close() {
		log.debug("Shutting down api");
		session.close();
		Session.shutdown_api();		
		log.debug("Api shutdown complete");
	}
}
