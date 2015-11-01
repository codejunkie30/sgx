package com.wmsi.sgx.domain;

import java.util.Comparator;

public class SortAccountByExpirationDateComparator implements Comparator<Account>
{
	@Override
	public int compare(Account o1, Account o2) {
		return o2.getExpirationDate().compareTo(o1.getExpirationDate());
	}

}
