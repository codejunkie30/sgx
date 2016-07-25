package com.wmsi.sgx.repository;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.wmsi.sgx.domain.FXConversionMultiplerTable;

public interface FxConversionRepository extends CustomRepository<FXConversionMultiplerTable, Serializable>{
	
	List<FXConversionMultiplerTable> findBydateAfter(Date d);
}
