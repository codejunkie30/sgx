package com.wmsi.sgx.model;

import java.util.List;
import com.google.common.base.Objects;

public class Currencies {
	
	private List<String> currecyList;

	public List<String> getCurrecyList() {
		return currecyList;
	}

	public void setCurrecyList(List<String> currecyList) {
		this.currecyList = currecyList;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("currecyList", currecyList).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(currecyList);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Currencies) {
			Currencies that = (Currencies) object;
			return Objects.equal(this.currecyList, that.currecyList);
		}
		return false;
	}
	
	
}
