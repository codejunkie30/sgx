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

/**
 * This class create/validate transaction session token and verifies the token
 * is active or not.
 *
 */

@Service
public class TrasactionSessionTokenVerificationServiceImpl implements TrasactionSessionTokenVerificationService {

	@Autowired
	private TransactionSessionTokenRepository transactionSessionTokenReposistory;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;
	
	/**
	 * Creates the transaction session token.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return String
	 * 
	 */

	public String createTransactionSessionToken(User user, String transSessionToken) {
		/**
		 * minor time difference between creation time and expiration time
		 * because user expiration time is set before the invoking this API
		 */
		return createTransactionSessionToken(user, transSessionToken, new Timestamp(System.currentTimeMillis()),
				new Timestamp(user.getExpires())).getToken();
	}
	
	/**
	 * Creates the transaction session token.
	 * 
	 * @param user
	 *            User, transSessionToken String, creationTime Timestamp,
	 *            expiryTime Timestamp
	 * 
	 * @return String
	 * 
	 */

	private TransactionSessionVerification createTransactionSessionToken(User user, String transSessionToken,
			Timestamp creationTime, Timestamp expiryTime) {
		TransactionSessionVerification transSessVerification = new TransactionSessionVerification();
		transSessVerification.setUser_id(user.getId());
		transSessVerification.setToken(transSessionToken);
		transSessVerification.setCreationTime(creationTime);
		transSessVerification.setExpiryTime(expiryTime);
		transSessVerification.setTxSessionTokenStatus(true);// Enable the token
		return transactionSessionTokenReposistory.save(transSessVerification);

	}
	
	/**
	 * Validates the transaction session token.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return String
	 * 
	 * @throws TransactionSessionTokenVerificationException,
	 *             VerifiedTransactionSessionTokenPremiumException
	 */

	public boolean validateTransactionSessionToken(User user, String transSessionToken)
			throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException {
		// Verify if its valid token
		if (TrasactionSessionTokenVerificationServiceImpl.isNullOrEmpty(transSessionToken) || user == null)
			throw new TransactionSessionTokenVerificationException("Transaction token not found.");
		// Verify if active token exists
		TransactionSessionVerification transSessverification = transactionSessionTokenReposistory.findByTokenUserStatus(
				user.getId(), true/** interested only in active token **/
				, transSessionToken);

		if (transSessverification == null) {
			throw new TransactionSessionTokenVerificationException("Transaction token not found.");
		} else {
			long originalTime = System.currentTimeMillis();
			Timestamp originalTimeStamp = new Timestamp(originalTime);

			if (originalTimeStamp.after(transSessverification.getExpiryTime())
					|| originalTimeStamp.equals(transSessverification.getExpiryTime())) {
				throw new TransactionSessionTokenVerificationException("Transaction Session expired.");
			}
			return true;
		}
	}
	
	/**
	 * Verifies the token is expiring or not.
	 * 
	 * @param user
	 *            User, transSessionToken String
	 * 
	 * @return boolean
	 * 
	 */

	public boolean isTokenExpiring(User user, String transSessionToken) {
		TransactionSessionVerification transSessVerification = transactionSessionTokenReposistory
				.findByUserIDAndStatus(user.getId(), true);

		long originalTime = System.currentTimeMillis();
		Timestamp originalTimeStamp = new Timestamp(originalTime);

		Timestamp beforeExpireTime = decrementCurrentTimeBy3Minutes(transSessVerification);

		if (originalTimeStamp.after(beforeExpireTime)
				&& originalTimeStamp.before(transSessVerification.getExpiryTime())){
			System.out.println("originalTimeStamp = " + originalTimeStamp + " beforeExpireTime = " + beforeExpireTime
					+ " transSessverification.getExpiryTime() = " + transSessVerification.getExpiryTime());
				return true;
		}
		return false;
	}
	
	/**
	 * Decrements current time by three minutes.
	 * 
	 * @param transSessVerification
	 *            TransactionSessionVerification
	 * 
	 * @return Timestamp
	 * 
	 */

	private Timestamp decrementCurrentTimeBy3Minutes(TransactionSessionVerification transSessVerification) {
		Timestamp expiryTimeStamp = new Timestamp(transSessVerification.getExpiryTime().getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expiryTimeStamp.getTime());
		cal.add(Calendar.MINUTE, -3);
		expiryTimeStamp = new Timestamp(cal.getTime().getTime());
		return expiryTimeStamp;
	}

	/**
	 * Decrements current time by two minutes.
	 * 
	 * @param transSessVerification
	 *            TransactionSessionVerification
	 * 
	 * @return Timestamp
	 * 
	 */
	private Timestamp incrementCurrentTimeBy2Minutes() {
		long originalTime = System.currentTimeMillis();
		Timestamp expiryTimeStamp = new Timestamp(originalTime);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expiryTimeStamp.getTime());
		cal.add(Calendar.MINUTE, 2);
		expiryTimeStamp = new Timestamp(cal.getTime().getTime());
		return expiryTimeStamp;
	}

	/**
	 * Returns the token expiring time.
	 * 
	 * @return Timestamp
	 * 
	 */
	public Timestamp getTokenExpirationTime() {
		Timestamp expiryTimeStamp = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(expiryTimeStamp.getTime());
		cal.add(Calendar.MINUTE, 13);
		/** TODO REvalidate cal.getTime() vs cal.getTime().getTime() **/
		expiryTimeStamp = new Timestamp(cal.getTime().getTime());
		return expiryTimeStamp;
	}

	/**
	 * Deletes the transaction tokens based on user.
	 * 
	 * @param user
	 *            User
	 * @return int
	 */
	public int deleteTransactionSessionTokens(User user) {
		return transactionSessionTokenReposistory.deleteUserTransactionSessionTokens(user.getId());
	}

	/**
	 * Disables the transaction entries of the user.
	 * 
	 * @param user
	 *            User
	 * @return boolean
	 */
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
	
	/**
	 * Checks for the null object.
	 * @param myString String
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(String myString) {
		return myString == null || "".equals(myString);
	}

}
