package com.wmsi.sgx.service.account.impl;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TransactionSessionVerification;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.repository.TransactionSessionTokenRepository;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

@Service
public class TrasactionSessionTokenVerificationServiceImpl implements TrasactionSessionTokenVerificationService {

	@Autowired
	private TransactionSessionTokenRepository transactionSessionTokenReposistory;

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
			throw new TransactionSessionTokenVerificationException("Transaction token not found.");
		else
			return true;
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
