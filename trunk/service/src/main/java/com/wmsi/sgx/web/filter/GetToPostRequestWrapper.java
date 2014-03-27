package com.wmsi.sgx.web.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Convert a GET request to a POST with a body using a query string value as
 * the POST body  
 * @author JLee
 *
 */
public class GetToPostRequestWrapper extends HttpServletRequestWrapper{

	private String param;
	private String contentType = MediaType.APPLICATION_JSON_VALUE;
			
	/**
	 * Covert the given request to a POST.
	 * @param request - target request
	 * @param param - name of query string parameter to convert to POST body.
	 */
	public GetToPostRequestWrapper(HttpServletRequest request, String param){
		super(request);	
		this.param = param;
	}

	/**
	 * Covert the given request to a POST.
	 * @param request - target request
	 * @param param - name of query string parameter to convert to POST body.
	 * @param contentType - content type to use for POST request
	 */
	public GetToPostRequestWrapper(HttpServletRequest request, String param, String contentType){
		this(request, param);
		this.contentType = contentType;		
	}

	@Override
	public String getMethod(){
		return HttpMethod.POST.name();
	}

	@Override
	public String getContentType(){
		return contentType;
	}
	
	@Override
	public ServletInputStream getInputStream(){
		
		byte[] body = null;
		String json = getParameter(param);
		
		if(json != null)
			body = json.getBytes();
		
		return new LocalInputStream(new ByteArrayInputStream(body));	
	}

	class LocalInputStream extends ServletInputStream{

		private InputStream in;
		
		public LocalInputStream(InputStream i){
			in = i;
		}
		
		@Override
		public int read() throws IOException {
			return in.read();
		}
	}
}
