package com.wmsi.sgx.service.account;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;

public interface TrasactionSessionTokenVerificationService
{
  public String createTransactionSessionToken(User user);

  public String verifyTransactionSessionToken(User user,String token) throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException;
  
  public Response deleteTransactionSessionTokens(User user);
  
}
