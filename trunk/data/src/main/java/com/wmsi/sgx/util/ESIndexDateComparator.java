package com.wmsi.sgx.util;

import java.util.Comparator;
import java.util.Date;

import com.wmsi.sgx.model.indexer.Index;

public class ESIndexDateComparator implements Comparator<Index>{

	@Override
	public int compare(Index o1, Index o2) {
		//return (new Date(Long.parseLong(arg1))).compareTo(new Date(Long.parseLong(arg0)));
		
		return 0;
	}

	
}
