package com.wmsi.sgx.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.wmsi.sgx.security.token.TransactionTokenAuthenticationService;

public class TransactionTimeCheckController
{
  @Autowired
  private TransactionTokenAuthenticationService transactionTokenAuthenticationService;  
  
  private String transactionToken = "";
  
  protected boolean isValidTransactionToken()
  {
//    TransactionTokenHandler transactionTokenHandler = transactionTokenAuthenticationService.getTransactionTokenHandler();
//    boolean isValidToken = transactionTokenHandler.isTransactionTokenValid(getTransactionToken());
//    return isValidToken;
    return true;
  }

  public String getTransactionToken()
  {
    return transactionToken;
  }

  public void setTransactionToken( String transactionToken )
  {
    this.transactionToken = transactionToken;
  }
  
  
 
}