package com.wmsi.sgx.logging;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.wmsi.sgx.service.sandp.capiq.CapIQRequestException;

@Component
@Aspect
public class LoggingAspect{
	
	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	
	/** 
	 * Logging advice to time request times from CapIQ api requests. 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution( * com.wmsi.sgx.service.sandp.capiq.RequestExecutor.execute(..))")
	public Object capIQRequestExecutorProfiling(ProceedingJoinPoint pjp) throws Throwable {
		
		Stopwatch s = new Stopwatch();
		s.start();

		// Let the method run
		Object retVal = pjp.proceed();
		
		s.stop();
		
		log.trace("CapIQRequest executed in: {} millis ", s.elapsed(TimeUnit.MILLISECONDS));
		
		return retVal;
	}
	
	@Autowired
	private ObjectMapper capIqObjectMapper;
	
	/**
	 * Advice to log CapIQ response json if an exception is thrown during response conversion. 
	 * @param jp
	 * @param e
	 */
	@AfterThrowing(pointcut="execution( * com.wmsi.sgx.service.sandp.capiq.ResponseParser.convert(..))", throwing = "e")
	public void responseParserConvertExceptionAdvice(JoinPoint jp, Throwable e) {

		Signature signature = jp.getSignature();
	    String methodName = signature.getName();
	    Object[] args = jp.getArgs();
	    
	    String response = null;
	    
	    if(args != null && args[0] != null){
	    	try{
	    		// Convert the response object back to json for better representation of the raw response 
				response = capIqObjectMapper.writeValueAsString(jp.getArgs()[0]);				
			}
			catch(JsonProcessingException je){
				log.debug("Error converting object to json in logger.", je);
			}
	    }

	    if(response ==  null)
	    	// Default to simple to string if json conversion fails
	    	response = Arrays.toString(args);

	    log.error("Exception throw from response parser for method '{}' response: {}", methodName, response);
	}
	
	/**
	 * After returning advice for logging queries post binding. 
	 * @param retVal
	 */
	@AfterReturning(pointcut="execution( * com.wmsi.sgx.service.sandp.capiq.CapIQRequest.buildQuery(..))", returning="retVal")
	public void requestBuildQueryAdivce(Object retVal){
		
		log.trace("CapIQRequest buildQuery: {}", retVal);
		
	}
	
	/**
	 * Advice for detailed logging of request/response data
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution( * com.wmsi.sgx.service.sandp.capiq.AbstractDataService.executeRequest(..))")
	public Object requestExecutorAdivce(ProceedingJoinPoint pjp) throws Throwable{
	    String args = Arrays.toString(pjp.getArgs());

		log.trace("Executing dataService request");
		log.trace("Args: {}", args);
		
		Object obj = pjp.proceed();
		
		if(obj != null)
			log.trace("Response {}", obj.toString());
		
		return obj;
	}

	/**
	 * Advice for detailed logging of request/response data on exceptions from RequestExecutor
	 * @param jp
	 * @param e
	 */
	@AfterThrowing(pointcut="execution( * com.wmsi.sgx.service.sandp.capiq.RequestExecutor.execute(..))", throwing = "e")
	public void requestExecutorExceptionAdvice(JoinPoint jp, Throwable e) {

		if( e instanceof CapIQRequestException){

			CapIQRequestException ex = (CapIQRequestException) e;
			
			log.debug("CapIQ Request exception.\n\n" +
				"Response Code: {} {} \n\n" +	
				"Request:\n---------------------------------------------------------\n\n" +
				"Headers:\n {} \n\nBody:\n {} \n\n" +
				"Response:\n---------------------------------------------------------\n\n" +
				"Headers:\n {} \n\nBody:\n {} \n\n ",
				ex.getStatusCode(),	ex.getStatusText(),	
				ex.getRequestHeaders(),	ex.getRequestBody(),
				ex.getResponseHeaders(), ex.getResponseBody());
		}
		
	}

}
