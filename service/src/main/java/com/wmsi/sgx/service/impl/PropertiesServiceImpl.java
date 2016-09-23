package com.wmsi.sgx.service.impl;


import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.domain.Configurations;
import com.wmsi.sgx.domain.ConfigurationsAudit;
import com.wmsi.sgx.repository.ConfigurationsAuditRepository;
import com.wmsi.sgx.repository.ConfigurationsRepository;
import com.wmsi.sgx.service.PropertiesService;

@Service
public class PropertiesServiceImpl implements PropertiesService{
	
	private PropertiesConfiguration configuration;
	
	@Value ("${spring.profiles.active:dummy}")
	private String envType;
	
	@Autowired
	public Environment env;
	
	@Autowired
	public ConfigurationsRepository configurationsRepository;
	
	@Autowired
	public ConfigurationsAuditRepository configurationsAuditRepository;

	@PostConstruct
	public void init() {		
		String filePath = "";		
		switch(envType){
			case("prod-sing"):
				filePath = "META-INF/properties/prod-sing.application.properties";
			case("prod-us"):
				filePath = "META-INF/properties/prod-us.application.properties";
			default:
				filePath = "META-INF/properties/application.properties";
		}
		
		try {
			configuration = new PropertiesConfiguration(filePath);
			//Create new FileChangedReloadingStrategy to reload the properties file based on the given time interval 
			FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
			fileChangedReloadingStrategy.setRefreshDelay(1000);
			configuration.setReloadingStrategy(fileChangedReloadingStrategy);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public String getProperty(String key) {
		//return (String) configuration.getProperty(key);
		return (String) configurationsRepository.findByProperty(key).getValue();
	}
	@Override
	public void setProperty(String key, Object value, String username) {
		//configuration.setProperty(key, value);
		String value1 = (String) value;
		Configurations conf =  configurationsRepository.findByProperty(key);
		conf.setProperty(key);
		conf.setValue(value1);
		conf.setModifiedBy(username);
		conf.setModifiedDate(new Date());
		configurationsRepository.save(conf);
		
		//Auditing
		ConfigurationsAudit configurationsAudit = new ConfigurationsAudit();
		BeanUtils.copyProperties(conf,configurationsAudit);
		configurationsAuditRepository.save(configurationsAudit);
	}
	@Override
	public void save() {
		try {
			configuration.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
