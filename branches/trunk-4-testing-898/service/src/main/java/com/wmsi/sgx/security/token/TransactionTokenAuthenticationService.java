package com.wmsi.sgx.security.token;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

@Service
public class TransactionTokenAuthenticationService
{
  private static final String TRANSACTION_HEADER_NAME = "TRANS-AUTH-TOKEN";
  
  @Autowired
  private TrasactionSessionTokenVerificationService trasactionSessionTokenVerificatioService;
  


  
  public TransactionTokenAuthenticationService() {


  }
  
  public void addTransactionTokenAuthentication(HttpServletResponse response, User user) {
    try
    {
      String token = trasactionSessionTokenVerificatioService.verifyTransactionSessionToken( user, "" );
      if("".equals( token ))
      {
        token =  trasactionSessionTokenVerificatioService.createTransactionSessionToken( user,token );
        response.addHeader(TRANSACTION_HEADER_NAME, token);
      }
      else
      {
        response.addHeader(TRANSACTION_HEADER_NAME, token);
      }
    }
    catch(TransactionSessionTokenVerificationException exec)
    {
      
    }
    catch(VerifiedTransactionSessionTokenPremiumException exec)
    {
      
    }
    
  }
  
  public void validateTransactionSessionToken(HttpServletResponse response, User user,String token)
  {
    
    try
    {
      trasactionSessionTokenVerificatioService.verifyTransactionSessionToken( user,token);
    }
    catch(TransactionSessionTokenVerificationException exec)
    {
      
    }
    catch(VerifiedTransactionSessionTokenPremiumException exec)
    {
      
    }
  }
  
  
}
