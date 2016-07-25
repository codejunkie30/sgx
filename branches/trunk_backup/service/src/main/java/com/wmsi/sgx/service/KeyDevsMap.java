package com.wmsi.sgx.service;

import java.util.List;
import java.util.Map;

public interface KeyDevsMap {

	Map<String, List<String>> getMap();

	String getKeyDevLabel(String key);
	
	String getKeyDevLabelByType(String type);

}
