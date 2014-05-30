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

import com.google.common.base.Stopwatch;

@Aspect
public class LoggingAspect{
	
	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
	
	@Around("execution( * com.wmsi.sgx.service.sandp.capiq.RequestExecutor.execute(..))")
	public Object capIQRequestExecutorProfiling(ProceedingJoinPoint pjp) throws Throwable {
		
		Stopwatch s = new Stopwatch();
		s.start();
		
		Object retVal = pjp.proceed();		
		s.stop();
		
		log.debug("CapIQRequest executed in: {} millis ",s.elapsed(TimeUnit.MILLISECONDS));
		
		return retVal;
	}
	
	@AfterThrowing(pointcut="execution( * com.wmsi.sgx.service.sandp.capiq.ResponseParser+.convert(..))", throwing = "e")
	public void responseParserConvertExceptionAdvice(JoinPoint jp, Throwable e) {

		Signature signature = jp.getSignature();
	    String methodName = signature.getName();
	    String args = Arrays.toString(jp.getArgs());
	    
		log.error("Exception throw from repsonse parser for method '{}' args: {}", methodName, args);		

	}
	
	@AfterReturning(pointcut="execution( * com.wmsi.sgx.service.sandp.capiq.CapIQRequest.buildQuery(..))", returning="retVal")
	public void requestBuildQueryAdivce(Object retVal){
		
		log.trace("CapIQRequest buildQuery: {}", retVal);
		
	}
	
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
}
