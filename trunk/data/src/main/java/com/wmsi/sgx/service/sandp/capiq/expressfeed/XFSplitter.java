package com.wmsi.sgx.service.sandp.capiq.expressfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class XFSplitter {

	private static final Logger log = LoggerFactory.getLogger(XFSplitter.class);
	
	@Value("${loader.companies.dir}")
	private String companiesDir = "/mnt/data/companies/";
	
	@Value("${loader.raw.dir}")
	private String rawDir = "/mnt/data/raw/";
	
	@Value("${loader.ftp.rawFiles}")
	private String rawFiles = "company-data.csv,consensus-estimates.csv,dividend-history.csv,key-devs.csv,ownership.csv,adjustment-factor.csv";
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;
	
	/**
	 * Init method for splitting raw data files based on companies  
	 * 
	 * @return boolean 
	 */
	public Boolean init() {
		
		try {
			
			String[] rawFileNames = rawFiles.split(",");
			for (String rawFile : rawFileNames) {
				File f = new File(rawDir+rawFile);
				if (f.exists()) {
					log.info("Splitting File: " + f.getAbsolutePath());
					Map<String, ArrayList<String>> companies = new HashMap<String, ArrayList<String>>();
					String key = null;

					try (BufferedReader br = new BufferedReader(new FileReader(f))) {
						for (String line; (line = br.readLine()) != null;) {
							String tmp = getKey(line);
							if (tmp != null)
								key = tmp;
							if (key != null) {
								if (companies.get(key) == null)
									companies.put(key, new ArrayList<String>());
								companies.get(key).add(line);
							}
						}
					}

					String name = f.getName();
					writeFiles(companies, name.substring(0, name.indexOf(".")));
				}
			}
			
			
			
			return true;
			
		}
		catch(Exception e) {
			errorBeanHelper
					.addError(new ErrorBean("XFSplitter:init", "Splitting XF Files", ErrorBean.ERROR, errorBeanHelper.getStackTrace(e)));
			log.error("Splitting XF Files", e);
		}
		
		return false;
	}
	
	public String getKey(String line) {
		
		if (line == null || line.trim().length() == 0 || !line.startsWith("\"") || line.startsWith("\",")) return null;
		
		try {
			String key = line.substring(0, line.indexOf(",", line.indexOf(",")+1));
			return key;
		}
		catch(Exception e) {}
		
		return null;
	}
	
	public void writeFiles(Map<String,ArrayList<String>> companies, String append) throws Exception {
		
		log.info("Creating company " + append + " files");
		
		for (String key : companies.keySet()) {
			File f = new File(companiesDir + append + "/" + key.replace("\"", "").replace(",", "-") + ".csv");
			FileUtils.writeLines(f, companies.get(key));
		}
		
	}
	
	
	public static void main(final String... args) throws Exception {
		
		new XFSplitter().init();
		
	}
	
	
	
}