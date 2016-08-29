package com.wmsi.sgx.security.token;

import java.sql.Timestamp;
import java.util.Calendar;

import com.wmsi.sgx.domain.User;

public class TransactionTokenHandler
{
  public String createTransactionTokenForUser(User user) {
    
    
    return "sgx";
  }
  
  private String getToken(User user)
  {
    return "";
  }
  public boolean isTransactionTokenValid(String transactionToken)
  {
    String[] tokenInfo = parseToken(transactionToken);
    isValid(tokenInfo[0],tokenInfo[1]);
    return true;
  }
  
  private String[] parseToken(String transactionToken) 
  {
    String[] tokenInfo=null;
    return tokenInfo;
  }
  
  private boolean isValid(String transactionToken,String userID)
  {
    long currentDate = System.currentTimeMillis();

    Timestamp currentTimeStamp = new Timestamp(currentDate);
    
    Timestamp userTimeStamp = new Timestamp(1472019027246l);
    
    Timestamp expireTimeStamp = new Timestamp(1472019027246l);
    
    if(currentTimeStamp.after( userTimeStamp ) && currentTimeStamp.before(expireTimeStamp))
    {
      return true;
    }

    return false;
        
  }
  
  private void insertTransactionTokenForUser(User user,String transactionToken)
  {
    
  }
  
}
