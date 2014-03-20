package com.wmsi.sgx.service.sandp.alpha.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;

@Service
public class AlphaFactorIndexerServiceImpl implements AlphaFactorIndexerService{

	public List<AlphaFactor> loadAlphaFactors(File f){

		StopWatch s = new StopWatch();
		s.start();
		List<AlphaFactor> ret = new ArrayList<AlphaFactor>();
		
		try{
			// TODO use dozer or some kind of csv file mapper
			List<String> lines = FileUtils.readLines(f);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			for(String line : lines.subList(1,lines.size())){
				String[] tokens = line.split("\t");
					
				if(tokens.length < 47)
					continue;
				AlphaFactor af = new AlphaFactor();
				af.setId(tokens[3]);
				af.setDate(sdf.parse(tokens[0]));
				af.setPriceMomentum(NumberUtils.toInt(tokens[40]));				
				af.setHistoricalGrowth(NumberUtils.toInt(tokens[41]));
				af.setCapitalEfficiency(NumberUtils.toInt(tokens[42]));
				af.setValuation(NumberUtils.toInt(tokens[43]));
				af.setEarningsQuality(NumberUtils.toInt(tokens[44]));
				af.setSize(NumberUtils.toInt(tokens[45]));
				af.setVolatility(NumberUtils.toInt(tokens[46]));
				
				if(tokens.length > 47)
					af.setAnalystExpectations(NumberUtils.toInt(tokens[47]));

				ret.add(af);
			}
		}
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(ParseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.stop();
		System.out.println(s.getTotalTimeMillis());
		return ret;
	}

}
