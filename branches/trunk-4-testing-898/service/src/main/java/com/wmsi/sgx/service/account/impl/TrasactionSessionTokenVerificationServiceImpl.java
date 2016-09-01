package com.wmsi.sgx.service.account.impl;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TransactionSessionVerification;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.TransactionSessionTokenRepository;
import com.wmsi.sgx.security.token.TokenAuthenticationService;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

@Service
public class TrasactionSessionTokenVerificationServiceImpl implements TrasactionSessionTokenVerificationService {

	@Autowired
	private TransactionSessionTokenRepository transactionSessionTokenReposistory;
	
	@Autowired
  private TokenAuthenticationService tokenAuthenticationService;

	public String createTransactionSessionToken(User user, String transSessionToken) {
		TransactionSessionVerification transSessVerification = new TransactionSessionVerification();
		transSessVerification.setUser_id(user.getId());
		transSessVerification.setToken(transSessionToken);
		transSessVerification.setCreationTime(new Timestamp(System.currentTimeMillis()));
		/**
		 * minor time difference between creation time and expiration time
		 * because user expiration time is set before the invoking this API
		 */
		transSessVerification.setExpiryTime(new Timestamp(user.getExpires()));
		transSessVerification.setTxSessionTokenStatus(true);// Enable the token
		transactionSessionTokenReposistory.save(transSessVerification);
		return transSessVerification.getToken();
	}

	public boolean validateTransactionSessionToken(User user, String transSessionToken)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException {
		// Verify if its valid token
		if (TrasactionSessionTokenVerificationServiceImpl.isNullOrEmpty(transSessionToken) || user == null)
			throw new TransactionSessionTokenVerificationException("Transaction token not found.");
		// Verify if active token exists
		TransactionSessionVerification transSessverification = transactionSessionTokenReposistory.findByTokenUserStatus(
				user.getId(), true/** interested only in active token **/,transSessionToken
		);

		if (transSessverification == null)
		{
			throw new TransactionSessionTokenVerificationException("Transaction token not found.");
		}
		else
		{
		  TransactionSessionVerification transSessVerification = transactionSessionTokenReposistory.findByUserIDAndStatus(user.getId(),true);
	    
	    long originalTime = System.currentTimeMillis();
	    Timestamp originalTimeStamp = new Timestamp(originalTime);
	    
	    if(originalTimeStamp.after(transSessVerification.getExpiryTime()) || originalTimeStamp.equals( transSessVerification.getExpiryTime() ))
	    {
	      throw new TransactionSessionTokenVerificationException("Transaction Session expired.");
	    }
			return true;
		}
	}

	public String preIncreementSessionTime(User user, String transSessionToken)
	{
	  TransactionSessionVerification transSessVerification = transactionSessionTokenReposistory.findByUserIDAndStatus(user.getId(),true);
	  
	  long originalTime = System.currentTimeMillis();
    Timestamp originalTimeStamp = new Timestamp(originalTime);
    
    Timestamp beforeExpireTime = decrementCurrentTimeBy2Minutes();
    
	  if(originalTimeStamp.after( beforeExpireTime ) && originalTimeStamp.before( transSessVerification.getExpiryTime() ))
	  {
	    System.out.println( "originalTimeStamp = "+originalTimeStamp+" beforeExpireTime = "+beforeExpireTime+" transSessverification.getExpiryTime() = "+transSessVerification.getExpiryTime() );
	    
      TransactionSessionVerification newTransSessverification = new TransactionSessionVerification();
      newTransSessverification.setUser_id( user.getId() );
      
      originalTime = System.currentTimeMillis();
      newTransSessverification.setCreationTime( new Timestamp(originalTime));
      
      newTransSessverification.setExpiryTime( getTokenExpirationTime() );
      newTransSessverification.setTxSessionTokenStatus( true );
      
      transSessionToken = tokenAuthenticationService.getTokenHandler().createTokenForUser( user );
      newTransSessverification.setToken( transSessionToken );
      
      transSessVerification.setTxSessionTokenStatus( false ); //old one
      transactionSessionTokenReposistory.save(newTransSessverification);
      
      transactionSessionTokenReposistory.save(transSessVerification); // old one saving
	  }
	  return transSessionToken;
	}
	
	private Timestamp decrementCurrentTimeBy2Minutes() {
    long originalTime = System.currentTimeMillis();
    Timestamp expiryTimeStamp = new Timestamp(originalTime);
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(expiryTimeStamp.getTime());
    cal.add(Calendar.MINUTE, -2);
    expiryTimeStamp = new Timestamp(cal.getTime().getTime());
    return expiryTimeStamp;
  }
	
	private Timestamp incrementCurrentTimeBy2Minutes() {
		long originalTime = System.currentTimeMillis();
		Timestamp expiryTimeStamp = new Timestamp(originalTime);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expiryTimeStamp.getTime());
		cal.add(Calendar.MINUTE, 2);
		expiryTimeStamp = new Timestamp(cal.getTime().getTime());
		return expiryTimeStamp;
	}

	public Timestamp getTokenExpirationTime() {
		Timestamp expiryTimeStamp = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expiryTimeStamp.getTime());
		cal.add(Calendar.MINUTE, 12);
		/** TODO REvalidate cal.getTime() vs cal.getTime().getTime() **/
		expiryTimeStamp = new Timestamp(cal.getTime().getTime());
		return expiryTimeStamp;
	}

	public int deleteTransactionSessionTokens(User user) {
		return transactionSessionTokenReposistory.deleteUserTransactionSessionTokens(user.getId());
	}

	@Override
	public boolean disableTransactionSessionToken(User user) {
		// Verify if its valid token
		TransactionSessionVerification transSessverification = transactionSessionTokenReposistory.findByUserIDAndStatus(
				user.getId(), true/** interested only in active token **/
		);
		if (transSessverification == null)
			return false;
		transSessverification.setTxSessionTokenStatus(false);
		return transactionSessionTokenReposistory.save(transSessverification) != null;
	}

	private static boolean isNullOrEmpty(String myString) {
		return myString == null || "".equals(myString);
	}

}
