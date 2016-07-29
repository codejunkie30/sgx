package com.wmsi.sgx.service.conversion;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModelMapper{

	private Mapper dozerMappingBean;
	
	@Autowired
	public void setDozerMappingBean(Mapper m){dozerMappingBean = m;}

	public <T> Object map(Object src, Class<T> clz){
		return dozerMappingBean.map(src, clz);
	}
	
	public <T> List<T> mapList(List<?> src, Class<T> clz){
		
		List<T> ret = new ArrayList<T>();
		
		for(Object t : src){
			ret.add(dozerMappingBean.map(t, clz));	
		}
		
		return ret;
	}

}
