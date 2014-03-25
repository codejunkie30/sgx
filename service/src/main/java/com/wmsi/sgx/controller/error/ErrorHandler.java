package com.wmsi.sgx.controller.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.wmsi.sgx.service.quanthouse.InvalidInstrumentException;

@ControllerAdvice
public class ErrorHandler{
	
	private Logger log = LoggerFactory.getLogger(ErrorHandler.class);
	
	@ExceptionHandler(InvalidInstrumentException.class )
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleInvalidInstrumentException(InvalidInstrumentException e) {
		log.error("Invalid ID requested");
		return new ErrorResponse(new ErrorMessage("Invalid ID", 4001));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class )
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleInvalidInstrumentException(MethodArgumentNotValidException e) {
		log.error("Invalid field name requested");
		return new ErrorResponse(new ErrorMessage("Invalid property", 4002));
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleException(Throwable e) {
		log.error("Caught unkown exception", e);
		return new ErrorResponse(new ErrorMessage("Bad Request", 5001));	    
	}	

}
