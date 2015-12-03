package com.wmsi.sgx.domain;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

public class SortAccountByExpirationDateComparator implements Comparator<Account>
{
	@Override
	public int compare(Account o1, Account o2) {
		return ObjectUtils.compare(o2 == null ? null : o2.getExpirationDate(), o1 == null ? null : o1.getExpirationDate());
	}

}
