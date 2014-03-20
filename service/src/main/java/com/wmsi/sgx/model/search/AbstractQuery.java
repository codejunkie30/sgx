package com.wmsi.sgx.model.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
@JsonTypeInfo(  
	    use = JsonTypeInfo.Id.NAME,  
	    include = JsonTypeInfo.As.PROPERTY,
	    property="type")
@JsonSubTypes({ 
    @Type(value = RangeQuery.class, name="range"),  
    @Type(value = TermQuery.class, name = "term")
	})*/
public abstract class AbstractQuery implements Query{

		
	public String buildQuery(){return null;}
}
