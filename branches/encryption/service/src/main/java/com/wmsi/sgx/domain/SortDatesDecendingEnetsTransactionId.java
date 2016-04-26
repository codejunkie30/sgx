package com.wmsi.sgx.domain;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

public class SortDatesDecendingEnetsTransactionId implements Comparator<EnetsTransactionDetails> {
	
	@Override
	public int compare(EnetsTransactionDetails o1, EnetsTransactionDetails o2) {
		return ObjectUtils.compare(o2 == null ? null : o2.getTrans_dt(), o1 == null ? null : o1.getTrans_dt());
	}

}
