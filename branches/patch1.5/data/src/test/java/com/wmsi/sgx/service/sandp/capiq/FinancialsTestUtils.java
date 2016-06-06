package com.wmsi.sgx.service.sandp.capiq;

import static org.testng.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.testng.TestException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wmsi.sgx.model.Financial;
import com.wmsi.sgx.model.Financials;
import com.wmsi.sgx.model.sandp.capiq.CapIQResponse;
import com.wmsi.sgx.util.test.TestUtils;

public class FinancialsTestUtils{

	public static CapIQResponse getFinancialsResponse(){
		try{
			ObjectMapper mapper = TestUtils.getObjectMapper();
			Resource json = new ClassPathResource("data/capiq/financialsResponse.json");
			return mapper.readValue(json.getInputStream(), CapIQResponse.class);
		}
		catch(IOException e){
			throw new TestException("Failed to intialize financials object mapper", e );
		}
	}

	public static void verify(Financials financials) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		
		assertNotNull(financials);
		assertNotNull(financials.getFinancials());
		assertEquals(financials.getFinancials().size(), 6);

		sortFinancials(financials);

		assertNotNull(financials.getFinancials().get(5));
		Financial ltm = financials.getFinancials().get(5);

		assertEquals(ltm.getAbsPeriod(), "LTM42013");
		assertEquals(ltm.getPeriodDate(), fmt.parse("2013-12-31"));
		assertEquals(ltm.getFilingCurrency(), "CNY");
		assertEquals(ltm.getTotalRevenue(), 6325.799D);
		assertNull(ltm.getDividendsPerShare());

		assertNotNull(financials.getFinancials().get(4));
		Financial fy2013 = financials.getFinancials().get(4);
		assertEquals(fy2013.getAbsPeriod(), "FY2013");
		assertEquals(fy2013.getPeriodDate(), fmt.parse("2013-12-31"));
		assertEquals(fy2013.getEps(), 0.634513D);

		assertNotNull(financials.getFinancials().get(3));
		Financial fy2012 = financials.getFinancials().get(3);
		assertEquals(fy2012.getAbsPeriod(), "FY2012");
		assertEquals(fy2012.getPeriodDate(), fmt.parse("2012-12-31"));
		assertEquals(fy2012.getGrossProfit(), 531.028000D);

		assertNotNull(financials.getFinancials().get(2));
		Financial fy2011 = financials.getFinancials().get(2);
		assertEquals(fy2011.getAbsPeriod(), "FY2011");
		assertEquals(fy2011.getPeriodDate(), fmt.parse("2011-12-31"));
		assertEquals(fy2011.getNetIncome(), 513.610000D);

		assertNotNull(financials.getFinancials().get(1));
		Financial fy2010 = financials.getFinancials().get(1);
		assertEquals(fy2010.getAbsPeriod(), "FY2010");
		assertEquals(fy2010.getPeriodDate(), fmt.parse("2010-12-31"));
		assertEquals(fy2010.getDividendsPerShare(), 0.051450D);
		assertEquals(fy2010.getTotalLiability(), 1897.137000D);

		assertNotNull(financials.getFinancials().get(0));
		Financial fy2009 = financials.getFinancials().get(0);
		assertEquals(fy2009.getAbsPeriod(), "FY2009");
		assertEquals(fy2009.getPeriodDate(), fmt.parse("2009-12-31"));
		assertEquals(fy2009.getCommonStock(), 368.583000D);
		assertNull(fy2009.getMinorityInterest());
		assertEquals(fy2009.getCashOperations(), 147.461000D);

	}

	private static void sortFinancials(Financials fins) {
		Collections.sort(fins.getFinancials(), new Comparator<Financial>(){
			@Override
			public int compare(Financial o1, Financial o2) {
				return o1.getAbsPeriod().compareTo(o2.getAbsPeriod());
			}

		});
	}
}
