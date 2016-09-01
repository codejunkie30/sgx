package com.wmsi.sgx.service.account;

import java.sql.Timestamp;

import com.wmsi.sgx.domain.User;

public interface TrasactionSessionTokenVerificationService
{
  public String createTransactionSessionToken(User user,String token);

  public boolean validateTransactionSessionToken(User user,String token) throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException;
  
  public int deleteTransactionSessionTokens(User user);
  
  public boolean disableTransactionSessionToken(User user);
  
  public Timestamp getTokenExpirationTime();
  
  public boolean isTokenExpiring(User user, String transSessionToken);
  
}
