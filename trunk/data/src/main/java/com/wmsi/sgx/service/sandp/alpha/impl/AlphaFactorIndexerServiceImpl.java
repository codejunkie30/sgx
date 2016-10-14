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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.wmsi.sgx.model.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;

@Service
public class AlphaFactorIndexerServiceImpl implements AlphaFactorIndexerService{

	private static final Logger log = LoggerFactory.getLogger(AlphaFactorIndexerServiceImpl.class);

	@Autowired
	private DefaultFtpSessionFactory ftpSessionFactory;

	private static final String ALPHA_FILE_PREFIX = "rank_";
	
	private static List<String> EXISTING_IDS = new ArrayList<String>();

	@Value("${loader.workdir}")
	private String tmpDir;
	
	@Value("${capiq.ftp.dir}")
	private String remoteDir;
	
	/**
	 * Retrieve latest AlphaFactor file
	 *
	 * @return File
	 * 
	 * @throws AlphaFactorServiceException
	 */
	@Override
	public File getLatestFile() throws AlphaFactorServiceException {
		
		Session<FTPFile> session = ftpSessionFactory.getSession();
		
		

		try{
			List<String> names = new ArrayList<String>();
			
			// Find rank files
			for(String n : session.listNames(remoteDir)){
				if(n.toLowerCase().startsWith(ALPHA_FILE_PREFIX)) names.add(n);
			}

			// Sort by name, which includes date, get latest file
			Collections.sort(names);
			String fileName = names.get(names.size() - 1);
			
			if(tmpDir == null){				
				tmpDir = System.getProperty("java.io.tmpDir");
			}
			
			File dir = new File(tmpDir);
			if(!dir.exists() && !dir.mkdirs()) throw new IOException("Failed to create tmp directory " + dir.getAbsolutePath());

			// Transfer
			File ret = new File(tmpDir + fileName);
			
			log.info("Downloading {} from {}", ret.getAbsolutePath(), remoteDir);
			
			FileOutputStream out = new FileOutputStream(ret);
			session.read(remoteDir + fileName, out);

			return ret;
		}
		catch(IOException e){
			throw new AlphaFactorServiceException("Exception downloading alpha factors file ", e);
		}
		finally{
			session.close();
		}
	}
	
	/**
	 * Check if an id is valid Alpha Company
	 * 
	 * @param alphaId
	 * @return Boolean
	 *
	 */
	@Override
	public boolean isAlphaCompany(String id) {
		return EXISTING_IDS.contains(id);
	}
	
	/**
	 * Loads AlphaFactors from Latest AlphaFactor file
	 * 
	 * @param AlphaFactor file provided by S&P
	 * @return List of AlphaFactors 
	 * 
	 * @throws AlphaFactorServiceException
	 */
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
				af.setCompanyId(toInt(record[4]));
				af.setDate(sdf.parse(record[0]));
				af.setPriceMomentum(toInt(record[41]));
				af.setHistoricalGrowth(toInt(record[42]));
				af.setCapitalEfficiency(toInt(record[43]));
				af.setValuation(toInt(record[44]));
				af.setEarningsQuality(toInt(record[45]));
				af.setSize(toInt(record[46]));
				af.setVolatility(toInt(record[47]));
				af.setAnalystExpectations(toInt(record[48]));
				
				EXISTING_IDS.add(record[4]);

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
	
	/**
	 * Utility method to get latest downloaded file from local Directory 
	 *
	 * @return AlphaFactor locally downloaded file
	 * 
	 * @throws IOException
	 */
	public File getLatestDownloadedFileFromLocalDirectory() throws IOException{
		File dir = new File(tmpDir);
		List<String> names = new ArrayList<String>();
		if(!dir.exists() && !dir.mkdirs()) throw new IOException("Failed to create tmp directory " + dir.getAbsolutePath());
		File []flist = dir.listFiles();
		for(File f:flist){
			names.add(FilenameUtils.getBaseName(f.getName()));
		}
		// Sort by name, which includes date, get latest file
		Collections.sort(names);
		String fileName = names.get(names.size() - 1);
		
		return new File(tmpDir + fileName);

	}

}
