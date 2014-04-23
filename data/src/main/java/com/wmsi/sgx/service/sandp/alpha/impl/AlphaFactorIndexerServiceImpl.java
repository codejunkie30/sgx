package com.wmsi.sgx.service.sandp.alpha.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;

@Service
public class AlphaFactorIndexerServiceImpl implements AlphaFactorIndexerService{

	@Autowired
	private DefaultFtpSessionFactory ftpSessionFactory;

	private static final String ALPHA_FILE_PREFIX = "rank_";

	@Value("${loader.workdir}")
	private String tmpDir;

	@Override
	public File getLatestFile() throws AlphaFactorServiceException {
		
		Session<FTPFile> session = ftpSessionFactory.getSession();

		try{
			List<String> names = new ArrayList<String>();
			
			// Find rank files
			for(String n : session.listNames("/")){
				if(n.toLowerCase().startsWith(ALPHA_FILE_PREFIX)){
					names.add(n);
				}
			}

			// Sort by name, which includes date, get latest file
			Collections.sort(names);
			String fileName = names.get(names.size() - 1);
			
			//if(tmpDir == null){				
				tmpDir = System.getProperty("java.io.tmpDir");
			//}
			
			// Transfer
			File ret = new File(tmpDir + fileName);
			FileOutputStream out = new FileOutputStream(ret);
			session.read(fileName, out);

			return ret;
		}
		catch(IOException e){
			throw new AlphaFactorServiceException("Exception downloading alpha factors file ", e);
		}
	}

	@Override
	public List<AlphaFactor> loadAlphaFactors(File f) throws AlphaFactorServiceException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		CSVReader reader = null;		
		List<AlphaFactor> ret = new ArrayList<AlphaFactor>();
		
		try{
			reader = new CSVReader(new FileReader(f), '\t');
			reader.readNext(); // skip header
			
			String[] record = null;
			
			 while((record = reader.readNext()) != null){

				AlphaFactor af = new AlphaFactor();
				af.setId(record[3]);
				af.setDate(sdf.parse(record[0]));
				af.setPriceMomentum(toInt(record[40]));
				af.setHistoricalGrowth(toInt(record[41]));
				af.setCapitalEfficiency(toInt(record[42]));
				af.setValuation(toInt(record[43]));
				af.setEarningsQuality(toInt(record[44]));
				af.setSize(toInt(record[45]));
				af.setVolatility(toInt(record[46]));
				af.setAnalystExpectations(toInt(record[47]));

				ret.add(af);
			}
		}
		catch(IOException | ParseException e){
			throw new AlphaFactorServiceException("Could not parse alpha factors file", e);
		}
		finally{
			IOUtils.closeQuietly(reader);				
		}

		return ret;
	}
	
	private int toInt(String str){
		return NumberUtils.toInt(str);
	}

}
