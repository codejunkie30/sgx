package com.wmsi.sgx.service.sandp.capiq.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListReader;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.service.sandp.capiq.AbstractDataService;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;
import com.wmsi.sgx.service.sandp.capiq.ResponseParserException;
import com.wmsi.sgx.util.TemplateUtil;

@SuppressWarnings("unchecked")
public class KeyDevsService extends AbstractDataService {
	
	private Logger log = LoggerFactory.getLogger(KeyDevsService.class);
	
	private ClassPathResource keyDevsDataTemplate = new ClassPathResource("META-INF/query/capiq/keyDevsData.json");
	private ClassPathResource requetWrapper = new ClassPathResource("META-INF/query/capiq/inputRequestsWrapper.json");
	
	@Value("${loader.key-devs.dir}")
	private String keyDevDir;
	
	@Value("${loader.raw.dir}")
	private String rawDir = "/mnt/data/raw/";
	
	public static final String KEY_DEV_SOURCE_COLUMN_NAME = "source";
	

	@Override	
	public KeyDevs load(String id, String... parms) throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		Assert.notEmpty(parms);
		KeyDevs devs = getKeyDevelopments(id);
		if (devs != null) devs.setTickerCode(tickerNoEx);
		return devs;
	}

	
	private String getQuery(List<String> ids) throws CapIQRequestException{

		ObjectMapper m;
		JsonNode requestWrapper;
		String json = null;
		try{
			m = new ObjectMapper();
			requestWrapper = m.readTree(requetWrapper.getInputStream());
			ArrayNode requestNode = (ArrayNode) requestWrapper.get("inputRequests");

			for(String id : ids){

				Map<String, Object> ctx = new HashMap<String, Object>();
				ctx.put("id", id);

				//String template = TemplateUtil.bind(keyDevsDataTemplate, ctx);
				char doublequote = '"';
				String templatebuff = 
					"{"+
							doublequote +"inputRequests"+doublequote+":"+ 
								"["+
									"{"+
							doublequote+"function"+doublequote+":"+doublequote+ "GDSP"+doublequote+","+
							doublequote+"identifier"+doublequote+":"+doublequote+ "$id$"+ doublequote+","+
							doublequote+"mnemonic"+doublequote+":"+doublequote+ "IQ_KEY_DEV_SOURCE"+doublequote+","+
									"}"+
								"]"+
							"}";
				ArrayNode arr = (ArrayNode) m.readTree(TemplateUtil.bind(templatebuff, ctx)).get("inputRequests");
				requestNode.addAll(arr);
			}
			
			json = m.writeValueAsString(requestWrapper);
		}
		catch(IOException e){
			log.error("Couldn't load key developments", e);
			throw new CapIQRequestException("Couldn't load key developments", e);
		}

		return json;
	}
	
	public KeyDevs getKeyDevelopments(String id)	throws ResponseParserException, CapIQRequestException {
		String tickerNoEx = id.split(":")[0];
		KeyDevs kD = new KeyDevs();
		kD.setTickerCode(tickerNoEx);
		Iterable<CSVRecord> records = getCompanyData(id, keyDevDir);
		if (records == null) return null;
		List<KeyDev> list = new ArrayList<KeyDev>();
		
		List<String> ids = new ArrayList<String>();
		for (CSVRecord record : records) {
			
			// remove duplicates
			if (ids.contains(record.get(2))) continue;
			ids.add(record.get(2));
			
			KeyDev keydev = new KeyDev();
			keydev.setSource(record.get(7));
			keydev.setDate(new Date(record.get(3)));
			keydev.setHeadline(record.get(4));
			keydev.setSituation(record.get(5));
			keydev.setType(record.get(6));
			list.add(keydev);
		}
		kD.setKeyDevs(list);
		return kD;
	}
	
	public Boolean init() {

		try {

			File f = new File(rawDir + keyDevDir + ".csv");
			if(!f.exists()){
				log.error("Unable to process key dev source content as key-devs.csv file doesn't exists");
				return false;
			}
			File backupFile = new File(rawDir + keyDevDir + "-bck.csv");
			if(backupFile.exists()){
				FileUtils.deleteQuietly(backupFile);
			}
			FileUtils.copyFile(f, backupFile);
			if (!backupFile.exists())
				return false;
			
			CSVHelperUtil csvHelperUtil = new CSVHelperUtil();
			Iterable<CSVRecord> records = csvHelperUtil.getRecords(backupFile.getAbsolutePath());
			List<String> idsForCapIqApiCall;
			Map<String, List<String>> tickerMap = new HashMap<>();
			for (CSVRecord record : records) {
				if(record.getRecordNumber() == 1) 
					continue;
				if(tickerMap.get(record.get(0))==null){
					idsForCapIqApiCall = new ArrayList<String>(); 
					tickerMap.put(record.get(0), idsForCapIqApiCall);
				}else{
					idsForCapIqApiCall = tickerMap.get(record.get(0));
				}
				idsForCapIqApiCall.add("IQKD"+record.get(2));
			}
			Map<String, String> keyDevSource = new HashMap<String, String>();
			for (Map.Entry<String, List<String>> entry : tickerMap.entrySet()) {

				String json = getQuery(entry.getValue());
				Resource template = new ByteArrayResource(json.getBytes());
				
				KeyDevs devs = null;
				try {
					devs = executeRequest(new CapIQRequestImpl(template), null);
				} catch (Exception e) {
					log.error("Key Dev Sources returned ---localized Message: {}-- from capIqService for ticker {}",
							e.getLocalizedMessage(), entry.getKey());
				}

				if (devs != null) {
					for (KeyDev dev : devs.getKeyDevs()) {
						keyDevSource.put(dev.getId(), dev.getSource());
					}
				}
			}
			loadSourceContent(keyDevSource);

		} catch (Exception e) {
			log.error("Couldn't load key developments", e);
		}
		return true;
	}
	
	private void loadSourceContent(Map<String, String> keyDevSource) throws IOException{
		String originalFilePath = new StringBuilder(rawDir).append(keyDevDir).append(".csv").toString();
		String backupFilePath = new StringBuilder(rawDir).append(keyDevDir).append("-bck.csv").toString();
		String tempFilePath = new StringBuilder(rawDir).append(keyDevDir).append("-temp.csv").toString();
		String originalBackupFilePath = new StringBuilder(rawDir).append(keyDevDir).append("-original.csv").toString();
		log.info("loading key developments source content");
		ICsvListReader listReader = null;
		ICsvListWriter listWriter = null;
		boolean success = true;
		try {
			listReader = new CsvListReader(new FileReader(backupFilePath),
					CsvPreference.STANDARD_PREFERENCE);
			List<String> columns;
			File tempFile = new File(tempFilePath);
			if(tempFile.exists()){
				FileUtils.deleteQuietly(tempFile);
			}
			listWriter = new CsvListWriter(new FileWriter(tempFilePath),
					CsvPreference.STANDARD_PREFERENCE);
			columns = listReader.read();
			columns.add(KEY_DEV_SOURCE_COLUMN_NAME);
			listWriter.write(columns);
			while ((columns = listReader.read()) != null) {
				columns.add(keyDevSource.get("IQKD" + columns.get(2)));
				listWriter.write(columns);
			}

		} catch (FileNotFoundException e) {
			success = false;
			log.error("The key-devs-bck.csv file is not exists ", e);
		} catch (IOException e) {
			success = false;
			log.error("Unable to read/write the key-dev source content into file", e);
		} finally {
			listReader.close();
			listWriter.close();
			if (success) {
				File f = new File(originalFilePath);
				File backupFile = new File(backupFilePath);
				File tempFile = new File(tempFilePath);
				File originalBackupFile = new File(originalBackupFilePath);
				FileUtils.deleteQuietly(backupFile);
				f.renameTo(originalBackupFile);
				tempFile.renameTo(f);
			}
		}
	}

}
