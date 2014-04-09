package com.wmsi.sgx.service.sandp.alpha.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.sandp.alpha.AlphaFactor;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorIndexerService;
import com.wmsi.sgx.service.sandp.alpha.AlphaFactorServiceException;

@Service
public class AlphaFactorIndexerServiceImpl implements AlphaFactorIndexerService{

	@Autowired
	private DefaultFtpSessionFactory ftpSessionFactory;

	private static final String ALPHA_FILE_PREFIX = "rank_";

	// TODO externalize
	private static final String tmpDir = "/mnt/feed/work/";

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

		List<AlphaFactor> ret = new ArrayList<AlphaFactor>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try{
			List<String> lines = FileUtils.readLines(f);
			
			for(String line : lines.subList(1, lines.size())){

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
		catch(IOException | ParseException e){
			throw new AlphaFactorServiceException("Could not parse alpah factors file", e);
		}

		return ret;
	}

}
