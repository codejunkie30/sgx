package com.wmsi.sgx.service.account.impl;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.TransactionSessionVerification;
import com.wmsi.sgx.domain.User;
import com.wmsi.sgx.model.Response;
import com.wmsi.sgx.repository.TransactionSessionTokenRepository;
import com.wmsi.sgx.security.token.TransactionTokenHandler;
import com.wmsi.sgx.service.account.TransactionSessionTokenVerificationException;
import com.wmsi.sgx.service.account.TrasactionSessionTokenVerificationService;
import com.wmsi.sgx.service.account.VerifiedTransactionSessionTokenPremiumException;

@Service
public class TrasactionSessionTokenVerificationServiceImpl implements TrasactionSessionTokenVerificationService
{
  @Autowired
  private TransactionSessionTokenRepository transactionSessionTokenReposistory;
  
 
  public String createTransactionSessionToken(User user)
  {
    TransactionSessionVerification transSessVerification = new TransactionSessionVerification();
    
    TransactionTokenHandler transactionTokenHandler = new TransactionTokenHandler();
    
    transSessVerification.setUser_id( user.getId() );
    
    String transSessionToken = transactionTokenHandler.createTransactionTokenForUser(user);
    transSessVerification.setToken( transSessionToken );
    
    long originalTime = System.currentTimeMillis();
    transSessVerification.setCreationTime( new Timestamp(originalTime));
    
    Timestamp expiryTimeStamp = new Timestamp(originalTime);
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(expiryTimeStamp.getTime());
    cal.add(Calendar.MINUTE, 12);
    expiryTimeStamp = new Timestamp(cal.getTime().getTime());
    transSessVerification.setExpiryTime( expiryTimeStamp );
    
    transSessVerification.setUserStatus( new Long(1) );
    
    
    transactionSessionTokenReposistory.save(transSessVerification);
    
    return transSessVerification.getToken();
  }

  public String verifyTransactionSessionToken(User user,String token) throws TransactionSessionTokenVerificationException, VerifiedTransactionSessionTokenPremiumException
  {
    TransactionTokenHandler transactionTokenHandler = new TransactionTokenHandler();
    TransactionSessionVerification transSessverification = null;
    String transSessionToken = "";
    if("".equals( token ))
    {
      transSessverification = transactionSessionTokenReposistory.findByUserIDAndStatus(user.getId(),new Long(1));
      if(transSessverification!=null)
      {
        if(new Timestamp(System.currentTimeMillis()).before(  transSessverification.getExpiryTime() ))
        {
          System.out.println( "User is active and in another browser" );
          transSessverification.getToken();
        }
        else
        {
          deleteTransactionSessionTokens(user);
          return "";
        }
      }
    }
    else
    {
      transSessverification = transactionSessionTokenReposistory.findByUserIDAndToken(user.getId(),token);
      
      if(transSessverification == null)
        throw new TransactionSessionTokenVerificationException("Transaction token not found.");
      
      long originalTime = System.currentTimeMillis();
      
      Timestamp expiryTimeStamp = new Timestamp(originalTime);
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(expiryTimeStamp.getTime());
      cal.add(Calendar.MINUTE, 2);
      expiryTimeStamp = new Timestamp(cal.getTime().getTime());
      transSessionToken = transSessverification.getToken();
      if(expiryTimeStamp.equals( transSessverification.getExpiryTime() ))
      {
        expiryTimeStamp = new Timestamp(originalTime);
        cal = Calendar.getInstance();
        cal.setTimeInMillis(expiryTimeStamp.getTime());
        cal.add(Calendar.MINUTE, 12);
        expiryTimeStamp = new Timestamp(cal.getTime().getTime());
        
        TransactionSessionVerification newTransSessverification = new TransactionSessionVerification();
        newTransSessverification.setUser_id( user.getId() );
        
        originalTime = System.currentTimeMillis();
        newTransSessverification.setCreationTime( new Timestamp(originalTime));
        
        newTransSessverification.setExpiryTime( expiryTimeStamp );
        newTransSessverification.setUserStatus( new Long(1) );
        transSessionToken = transactionTokenHandler.createTransactionTokenForUser(user);
        newTransSessverification.setToken( transSessionToken );
        
        transSessverification.setUserStatus( new Long(0) );
        
        transactionSessionTokenReposistory.save(newTransSessverification);
      }
         
      transactionSessionTokenReposistory.save(transSessverification);
    }
       
    return transSessionToken;
  }
  
  public Response deleteTransactionSessionTokens(User user)
  {
    Response response = new Response();
    TransactionSessionVerification[] transSessList = transactionSessionTokenReposistory.findByUserID(user.getId());
    for (TransactionSessionVerification list : transSessList) {
      if (list.getUser_id().equals(user.getId()) && (list.getExpiryTime().before( new Timestamp(System.currentTimeMillis())) || list.getUserStatus()==0)) {
        //TransactionSessionVerification transSessVerification = transactionSessionTokenReposistory.findByUserID(user.getId());
        transactionSessionTokenReposistory.delete(list);
        response.setMessage("success");
        return response;
      }
    }
    
    response.setMessage("failure");
    return response;
  
  }
}
