package com.wmsi.sgx.service.account;

/**
 * Creates/validates transaction session token and verifies the token is active or
 * not.
 *
 */

public class VerifiedTransactionSessionTokenPremiumException extends Exception
{
  private static final long serialVersionUID = 1L;

  public VerifiedTransactionSessionTokenPremiumException(String msg){
    super(msg);
  }
}
