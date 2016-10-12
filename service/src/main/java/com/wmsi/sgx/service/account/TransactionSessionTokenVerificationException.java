package com.wmsi.sgx.service.account;

/**
 * 
 * This class is used when exception occurs during Transaction Token
 * verification.
 *
 */
public class TransactionSessionTokenVerificationException extends Exception
{
  private static final long serialVersionUID = 1L;

  public TransactionSessionTokenVerificationException(String msg){
    super(msg);
  }
    
}
