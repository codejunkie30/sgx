/**
 * 
 */
package com.wmsi.sgx.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * Transaction Session Verification entity
 * 
 * @author dt78213
 */
@Entity(name = "TransactionSessionVerification")
@Table(name = "transactionSession_verification")
public class TransactionSessionVerification {
  @Id
  @GeneratedValue(generator = "transactionSessionVerificationGenerator")
  @GenericGenerator(name = "transactionSessionVerificationGenerator", strategy = "com.wmsi.sgx.generator.IDGenerator")
  private Long id;

  @Column(name = "user_id", nullable = false)
  public Long user_id;

  @Column(name = "token", nullable = false)
  public String token;

  @Column(name = "creationTime", nullable = false)
  public Date creationTime;
  
  @Column(name = "expiryTime", nullable = false)
  public Date expiryTime;

  @Column(name = "userStatus", nullable = false)
  public Long userStatus;

  public Long getId()
  {
    return id;
  }

  public void setId( Long id )
  {
    this.id = id;
  }

  public Long getUser_id()
  {
    return user_id;
  }

  public void setUser_id( Long user_id )
  {
    this.user_id = user_id;
  }

  public String getToken()
  {
    return token;
  }

  public void setToken( String token )
  {
    this.token = token;
  }

  public Date getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime( Date creationTime )
  {
    this.creationTime = creationTime;
  }

  public Date getExpiryTime()
  {
    return expiryTime;
  }

  public void setExpiryTime( Date expiryTime )
  {
    this.expiryTime = expiryTime;
  }

  public Long getUserStatus()
  {
    return userStatus;
  }

  public void setUserStatus( Long userStatus )
  {
    this.userStatus = userStatus;
  }

  @Override
  public String toString()
  {
    return "TransactionSessionVerification [id=" + id + ", user_id=" + user_id
           + ", token=" + token + ", creationTime=" + creationTime
           + ", expiryTime=" + expiryTime + ", userStatus=" + userStatus + "]";
  }

  

}
