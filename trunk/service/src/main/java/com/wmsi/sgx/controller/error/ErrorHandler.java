package com.wmsi.sgx.controller.error;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ErrorHandler{
	
	private Logger log = LoggerFactory.getLogger(ErrorHandler.class);
	
	@ExceptionHandler(MethodArgumentNotValidException.class )
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleInvalidInstrumentException(MethodArgumentNotValidException e) {
		
		StringBuilder errMsg = new StringBuilder("Validation failure. Errors: ");
		
		BindingResult binding = e.getBindingResult();
		if(binding != null && binding.hasErrors()){
			List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
			for(ObjectError err : allErrors){
				errMsg.append('[');
				errMsg.append(err.getDefaultMessage());
				errMsg.append("] ");
			}
		}
		
		log.debug("Validation error: {}", errMsg);
				
		return new ErrorResponse(new ErrorMessage(errMsg.toString(), 4002));
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)	
	public @ResponseBody ErrorResponse handleException(Throwable e) {
		log.error("Caught unkown exception", e);
		return new ErrorResponse(new ErrorMessage("Bad Request", 5001));	    
	}	

}
