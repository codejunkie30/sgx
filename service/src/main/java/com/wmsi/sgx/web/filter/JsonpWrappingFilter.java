package com.wmsi.sgx.web.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter for supporting jsonp by wrapping in jsonp callback.
 * Auto converts GET requests to POST with body.
 * @author JLee
 */
public class JsonpWrappingFilter extends OncePerRequestFilter{

	private static final Logger log = LoggerFactory.getLogger(JsonpWrappingFilter.class);

	private static final String JSON_PARAM_NAME = "json";
	private static final String JSONP_CALLBACK_PARAM = "callback";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		log.debug("Jsonp Wrapper Filter invoked");
		
		Map<String, String[]> parms = request.getParameterMap();
		
		if(parms.containsKey(JSON_PARAM_NAME) && parms.containsKey(JSONP_CALLBACK_PARAM)){

			// Execute request, writing response to wrapper so we can manipulate the results.
			GenericResponseWrapper wrapper = new GenericResponseWrapper(response);

			if (request.getMethod() == "GET") {

				log.debug("Converting jsonp GET request to POST with body");

				// Convert json query string value to request body
				GetToPostRequestWrapper postRequestWrapper = new GetToPostRequestWrapper(request, JSON_PARAM_NAME, MediaType.APPLICATION_JSON_VALUE);
				filterChain.doFilter(postRequestWrapper, wrapper);

			}
			
			String callback = parms.get(JSONP_CALLBACK_PARAM)[0];
			
			// Make sure callback parameter hasn't been compromised.  
			if(!validateJson(callback))
				throw new ServletException("Invalid characters found in jsonP callback parmameter. Possible XSS attempt");
			
			// Wrap json with jsonp callback
			OutputStream out = response.getOutputStream();
			
			out.write((callback + "(").getBytes());
			out.write(wrapper.getData());
			out.write(");".getBytes());

			wrapper.setContentType("text/javascript;UTF-8");

			out.close();
			
			log.debug("Jsonp request converted successfully");
			
		}
		else {			
			// Normal request
			filterChain.doFilter(request, response);
		}
	}

	// Regex to check json for any non word characters or javascript keywords
	private Pattern validJsonPattern = Pattern.compile("[^\\w\\$]|(alert|abstract|boolean|break|byte|case|catch|char|class|const|continue|debugger|default|delete|do|double|else|enum|export|extends|false|final|finally|float|for|function|goto|if|implements|import|in|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|super|switch|synchronized|this|throw|throws|transient|true|try|typeof|var|volatile|void|while|with|NaN|Infinity|undefined)");
	
	/**
	 * Validate json against injection attacks
	 */
	private boolean validateJson(String json){
		return !validJsonPattern.matcher(json).find();
	}
}
