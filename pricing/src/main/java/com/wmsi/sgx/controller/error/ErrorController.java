package com.wmsi.sgx.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
/**
 * Error Controller 
 */


@RequestMapping("/errors")
@RestController
public class ErrorController{

	@RequestMapping("*")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse notFound(){		
		return new ErrorResponse(new ErrorMessage("Bad Request", 5002));		
	}
}
