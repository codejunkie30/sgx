package com.wmsi.sgx.service.account;

/**
 * Thrown to indicate that transaction session token premium verification is falied
 *
 */
public class VerifiedTransactionSessionTokenPremiumException extends Exception
{
  private static final long serialVersionUID = 1L;

  public VerifiedTransactionSessionTokenPremiumException(String msg){
    super(msg);
  }
}
