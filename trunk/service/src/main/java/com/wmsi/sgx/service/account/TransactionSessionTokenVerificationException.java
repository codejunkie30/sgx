package com.wmsi.sgx.service.account;

/**
 * 
 * Thrown to indicate that the Transaction Token verification is failed
 *
 */
public class TransactionSessionTokenVerificationException extends Exception
{
  private static final long serialVersionUID = 1L;

  public TransactionSessionTokenVerificationException(String msg){
    super(msg);
  }
    
}
