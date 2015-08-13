package com.wmsi.sgx.controller.error;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.wmsi.sgx.service.account.InvalidTokenException;
import com.wmsi.sgx.service.account.UserExistsException;
import com.wmsi.sgx.service.account.UserVerificationException;

@ControllerAdvice
public class ErrorHandler{

	private Logger log = LoggerFactory.getLogger(ErrorHandler.class);

	@Autowired
	private MessageSource messages;
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
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

	@ExceptionHandler(UserExistsException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorResponse handleException(UserExistsException e) {

		log.debug("User Exists Exception.", e);
		
		return new ErrorResponse(
				new ErrorMessage(
					messages.getMessage(
						"user.exists", 
						null,  
						LocaleContextHolder.getLocale()),							
					4003));

	}

	@ExceptionHandler(UserVerificationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorResponse handleException(UserVerificationException e) {

		log.debug("User Verification Exception.", e);
		
		return new ErrorResponse(
					new ErrorMessage(
						messages.getMessage(
							"token.invalidToken", 
							null,  
							LocaleContextHolder.getLocale()),							
						4004));
	}

	@ExceptionHandler(InvalidTokenException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorResponse handleException(InvalidTokenException e) {

		log.debug("Invalid Token Exception.", e);
		
		return new ErrorResponse(
					new ErrorMessage(
						messages.getMessage(
							"token.invalidToken", 
							null,  
							LocaleContextHolder.getLocale()),							
						4005));
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody ErrorResponse handleException(Throwable e) {
		
		log.error("Caught unkown exception", e);
		
		return new ErrorResponse(
				new ErrorMessage(
					messages.getMessage(
						"error.unknown", 
						null,  
						LocaleContextHolder.getLocale()),							
					5001));
	}

}
