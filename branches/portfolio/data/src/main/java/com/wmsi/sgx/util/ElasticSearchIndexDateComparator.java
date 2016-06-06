package com.wmsi.sgx.util;

import java.util.Comparator;
import java.util.Date;


public class ElasticSearchIndexDateComparator implements Comparator<String> {


	@Override
	public int compare(String arg0, String arg1) {
		
		return (new Date(Long.parseLong(arg1))).compareTo(new Date(Long.parseLong(arg0)));
	}}
