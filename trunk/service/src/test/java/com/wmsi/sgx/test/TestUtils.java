package com.wmsi.sgx.test;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;

public class TestUtils{

	/**
	 * Util method for creating simple in memory cache manger for unit tests
	 * @param ttl - Time to live (in seconds)
	 * @param size - Cache max size
	 * @return
	 */
	public static CacheManager createSimpleCacheManager(final int ttl, final int size){
		ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(){

			@Override
			protected Cache createConcurrentMapCache(final String name) {
				return new ConcurrentMapCache(name, CacheBuilder
						.newBuilder()
						.expireAfterWrite(ttl, TimeUnit.SECONDS)
						.maximumSize(size).build().asMap(), false);
			}
		};

		return cacheManager;

	}
	
	public static String objectToJson(Object obj) throws JsonProcessingException, IllegalArgumentException{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(mapper.valueToTree(obj));		
	}
}
