package com.wmsi.sgx.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.wmsi.sgx.model.KeyDev;
import com.wmsi.sgx.model.KeyDevs;
import com.wmsi.sgx.model.StockListKeyDev;
import com.wmsi.sgx.model.keydevs.KeyDevsRequest;
import com.wmsi.sgx.model.keydevs.StockListKeyDevsRequest;
import com.wmsi.sgx.service.KeyDevsMap;
import com.wmsi.sgx.service.KeyDevsService;
import com.wmsi.sgx.service.ServiceException;
import com.wmsi.sgx.service.search.SearchService;
import com.wmsi.sgx.service.search.SearchServiceException;
import com.wmsi.sgx.service.search.elasticsearch.query.KeyDevsQueryBuilder;

@Service
public class KeyDevsServiceImpl implements KeyDevsService{

	@Autowired
	private SearchService keyDevsSearch;
	
	@Autowired
	private KeyDevsMap keyDevsMap;

	@Override
	@Cacheable("keyDevsSearch")
	public KeyDevs search(KeyDevsRequest req) throws ServiceException {

		try{
			KeyDevs keyDevs = keyDevsSearch.getById(req.getTickerCode(), KeyDevs.class);
			
			List<KeyDev> filtered = new ArrayList<KeyDev>();
			
			for(KeyDev dev : keyDevs.getKeyDevs()){
				Long devDate = dev.getDate().getTime();
				
				if(req.getTo() == null){
					DateFormat dF = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date();
					req.setTo(dF.format(date));
				}
				if (req.getFrom() == null)
					filtered.add(dev);
				else if(devDate.compareTo(getTime(req.getFrom())) > 0 
					&& devDate.compareTo(getTime(req.getTo())) < 0){
					
					filtered.add(dev);
				}		
			}
			
			keyDevs.setKeyDevs(filtered);
			
			return keyDevs;
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
	private long getTime(Object object){
		try{
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			return fmt.parse(object.toString()).getTime();
		}
		catch(ParseException e){
			
			return 0;
		}
	}
	
	@Override
	public List<KeyDevs> search(StockListKeyDevsRequest req) throws ServiceException {

		try{
			List<KeyDevs> keyDevs = keyDevsSearch.search(new KeyDevsQueryBuilder(req), KeyDevs.class).getHits();
			
			return keyDevs;
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
	@Override
	public Map<String, List<StockListKeyDev>> searchKeyDevs(StockListKeyDevsRequest req) throws ServiceException {

		try{
			Long from = null;
			Long to = null;
			Calendar calendar = Calendar.getInstance();
			from = calendar.getTime().getTime();
			calendar.add(Calendar.MONTH, -5);
			to = calendar.getTime().getTime();
			Map<String, StockListKeyDev> map = new HashMap<>();
			StockListKeyDev stockListKeyDev=null;
			List<KeyDevs> keyDevsList = keyDevsSearch.search(new KeyDevsQueryBuilder(req), KeyDevs.class).getHits();
			for (KeyDevs keyDevs : keyDevsList) {
				for (KeyDev keyDev : keyDevs.getKeyDevs()) {
					if (from.compareTo(keyDev.getDate().getTime()) > 0
							&& to.compareTo(keyDev.getDate().getTime()) < 0) {
						if (map.get(keyDev.getKeyDevId()) == null) {
							stockListKeyDev = new StockListKeyDev();
							map.put(keyDev.getKeyDevId(), stockListKeyDev);
							stockListKeyDev.setTickerCodes(new ArrayList<String>());
						} else {
							stockListKeyDev = map.get(keyDev.getKeyDevId());
						}
						BeanUtils.copyProperties(keyDev, stockListKeyDev);
						stockListKeyDev.getTickerCodes().add(keyDevs.getTickerCode());
						stockListKeyDev.setDate(keyDev.getDate().getTime());
					}
				}
			}
			return sortAndGroupByType(map);
			
		}
		catch(SearchServiceException e){
			throw new ServiceException("Query execution failed", e);
		}
	}
	
	private Map<String, List<StockListKeyDev>> sortAndGroupByType(Map map) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort( list, new Comparator<Map.Entry<String, StockListKeyDev>>()
        {
            public int compare( Map.Entry<String, StockListKeyDev> o1, Map.Entry<String, StockListKeyDev> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );

		Map<String, List<StockListKeyDev>> sortedHashMap = new TreeMap();
		List<StockListKeyDev> stockListKeyDevs;
		String keyDevTypeLabel;
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry<String, StockListKeyDev> entry = (Map.Entry<String, StockListKeyDev>) it.next();
			stockListKeyDevs = new ArrayList<>();
			keyDevTypeLabel = keyDevsMap.getKeyDevLabelByType(entry.getValue().getType());
			if(sortedHashMap.get(keyDevTypeLabel) == null){
				stockListKeyDevs = new ArrayList<>();
				sortedHashMap.put(keyDevTypeLabel, stockListKeyDevs);
			}else{
				stockListKeyDevs = sortedHashMap.get(keyDevTypeLabel);
			}
			stockListKeyDevs.add(entry.getValue());
		}
		return sortedHashMap;
	}
}
