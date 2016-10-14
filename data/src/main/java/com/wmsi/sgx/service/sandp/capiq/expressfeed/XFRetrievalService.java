package com.wmsi.sgx.service.sandp.capiq.expressfeed;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.wmsi.sgx.exception.ErrorBeanHelper;
import com.wmsi.sgx.model.ErrorBean;

public class XFRetrievalService {
	
	private static final Logger log = LoggerFactory.getLogger(XFRetrievalService.class);
	
	@Value("${loader.companies.dir}")
	private String companiesDir = "/mnt/data/companies/";
	
	@Value("${loader.base.dir}")
	private String baseDir = "/mnt/data/";
	
	@Value("${loader.raw.dir}")
	private String rawDir = "/mnt/data/raw/";
	
	@Value("${loader.ftp.location}")
	private String ftpLocation = "ftp.visitfc.com";

	@Value("${loader.ftp.username}")
	private String ftpUsername = "SGX";
	
	@Value("${loader.ftp.password}")
	private String ftpPassword = "8NTkcAy4Lm_k";
	
	@Value("${loader.ftp.port}")
	private int ftpPort = 22;
	
	@Value("${loader.ftp.baseFiles}")
	private String baseFiles = "companies.csv,currencies.csv,fx-conversion.csv,notfound.csv";
	
	@Value("${loader.ftp.rawFiles}")
	private String rawFiles = "company-data.csv,consensus-estimates.csv,dividend-history.csv,key-devs.csv,ownership.csv,adjustment-factor.csv";
	
	@Autowired
	private ErrorBeanHelper errorBeanHelper;

	@Autowired
	private com.wmsi.sgx.util.EmailService emailService;

	@Value ("${email.dataload.complete}")
	public String toSite;
	
	/**
	 * getSFTPSessionFactory
	 * @return DefaultSftpSessionFactory
	 */
	public DefaultSftpSessionFactory getSFTPSessionFactory(){
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
		factory.setHost(ftpLocation);
		factory.setPort(ftpPort);
		factory.setUser(ftpUsername);
		factory.setPassword(ftpPassword);		
		return factory;
	}
	
	/**
	 * Init method for grabbing latest files from FTP 
	 * and deleting old files
	 *  
	 * 
	 * @return boolean 
	 * @throws Exception
	 */
	public Boolean init() throws Exception {
		emailService.send(toSite," Starting dataload........","");
		String[] base = baseFiles.split(",");
		String[] raw = rawFiles.split(",");
		
		// delete the old files
		log.info("Removing feed old files");
		removeFiles(base, baseDir);
		removeFiles(raw, rawDir);
		removeFiles(new File(companiesDir).list(), companiesDir);
//		log.info("Finished removing old feed files");
		
		// grab the latest
		log.info("Retrieving new feed files from {}", ftpLocation);
		Session<LsEntry> session = getSFTPSessionFactory().getSession();
		grabFiles(session, base, baseDir);
		grabFiles(session, raw, rawDir);
		log.info("Finished retrieving new feed files from {}", ftpLocation);
		session.close();
		
		return true;
	}
	
	/**
	 * remove old files from mnt directory
	 * @param List of file names to be removed
	 * @param Local directory location
	 * @return 
	 * @throws Exception
	 */
	public void removeFiles(String[] names, String dir) throws Exception {
		
		if (names == null) return;
		
		for (String name : names) {
			File f = new File(dir + name);
			if (!f.exists()) {
				log.info("{} already removed, skipping", f.getAbsolutePath());
				continue;
			}
			FileUtils.deleteQuietly(f);
			log.info("Removed {}: {}", f.getAbsolutePath(), !f.exists());
		}
		
	}
	/**
	 * Grab files from FTP
	 * @param FTP Session entry
	 * @param List of file names to be grabbed
	 * @param Local directory location
	 * @return 
	 * @throws Exception
	 */
	public void grabFiles(Session<LsEntry> session, String[] names, String localDir) throws Exception {
		
		for (String name : names) {
			log.info("Retrieving {} from FTP", name);
			FileOutputStream out = new FileOutputStream(localDir + name); 
			try {
				session.read(name, out);
			}
			catch(Exception e) {
				errorBeanHelper
				.addError(new ErrorBean("XFRetrievalService:grabFiles", "Error in downloading file", ErrorBean.ERROR, e.getMessage()));
				errorBeanHelper.sendEmail();
				log.error("Downloading file: " + name, e);
			}
			finally {
				if (out != null) out.close();
			}
		}		
		
	}
	
	
	public static void main(final String... args) throws Exception {
		
		XFRetrievalService retr = new XFRetrievalService();
		retr.init();
		
	}
	
	
}