/*
 * FILE     : ErrorBean.java
 * 
 * CLASS    : ErrorBean
 * 
 * COPYRIGHT:
 * 
 * The computer systems, procedures, data bases and programs created and
 * maintained by DST Systems, Inc., are proprietary in nature and as such are
 * confidential. Any unauthorized use or disclosure of such information may
 * result is civil liabilities.
 * 
 * Copyright 2012 by DST Systems, Inc. All Rights Reserved.
 */
package com.wmsi.sgx.model;

/**
 * Bean class containing high-level information about an error.
 * 
 */
public class ErrorBean
{
    private String errorMessage;

    private String errorCode;
    
    private String errorSeverity;

    private String objectName;
    
    public static final String INFO = "INFO";
    
    public static final String WARNING = "WARNING";
            
    public static final String ERROR = "ERROR";

    
    //----------------------------------
    //  errorMessage
    //----------------------------------
    
	public ErrorBean(String errorCode,String errorMessage, String errorSeverity, String objectName) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.errorSeverity = errorSeverity;
		this.objectName = objectName;
	}
   
    public String getErrorMessage()
    {
        return errorMessage;
    }

    public ErrorBean setErrorMessage( String errorMessage )
    {
        this.errorMessage = errorMessage;
        return this;
    }

    //----------------------------------
    //  errorCode
    //----------------------------------
    
    public String getErrorCode()
    {
        return errorCode;
    }

    public ErrorBean setErrorCode( String errorCode )
    {
        this.errorCode = errorCode;
        return this;
    }

    //----------------------------------
    //  objectName
    //----------------------------------
    
    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName( String objectName )
    {
        this.objectName = objectName;
    }

    //----------------------------------
    //  errorSeverity
    //----------------------------------

    public void setErrorSeverity( String errorSeverity )
    {
        this.errorSeverity = errorSeverity;
    }

    //----------------------------------
    //  toString
    //----------------------------------
    
    @Override
    public String toString()
    {
        return "ErrorBean [errorMessage=" + errorMessage + ", errorCode="
                + errorCode + ", severity=" + errorSeverity + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((errorCode == null) ? 0 : errorCode.hashCode());
        result = prime * result
                + ((errorMessage == null) ? 0 : errorMessage.hashCode());
        result = prime * result
                + ((errorSeverity == null) ? 0 : errorSeverity.hashCode());
        result = prime * result
                + ((objectName == null) ? 0 : objectName.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        ErrorBean other = (ErrorBean) obj;
        if( errorCode == null )
        {
            if( other.errorCode != null )
                return false;
        }
        else if( !errorCode.equals( other.errorCode ) )
            return false;
        if( errorMessage == null )
        {
            if( other.errorMessage != null )
                return false;
        }
        else if( !errorMessage.equals( other.errorMessage ) )
            return false;
        if( errorSeverity == null )
        {
            if( other.errorSeverity != null )
                return false;
        }
        else if( !errorSeverity.equals( other.errorSeverity ) )
            return false;
        if( objectName == null )
        {
            if( other.objectName != null )
                return false;
        }
//        else if( !objectName.equals( other.objectName ) )
//            return false;
        return true;
    }
    
    

}