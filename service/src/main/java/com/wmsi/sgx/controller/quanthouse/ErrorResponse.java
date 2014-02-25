package com.wmsi.sgx.controller.quanthouse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.collect.ImmutableMap;

@JsonRootName(value = "error")
public class ErrorResponse{

	private String message;
	
	public ErrorResponse(String m){
		message = m;
	}
	
	public String getMessage(){return message;}
	
	 public ModelAndView asModelAndView() {
	        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
	        return new ModelAndView(jsonView, ImmutableMap.of("error", message));
	 }
}
