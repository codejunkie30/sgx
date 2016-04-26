package com.wmsi.sgx.web.filter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

/**
 * Servlet outputstream wrapper for maniuplating data after it's written to the servlet output stream
 * Inspired by (read: copied from) Oracle docs 
 * http://docs.oracle.com/cd/B32110_01/web.1013/b28959/filters.htm#BCFIAAAH
 *  
 * @author JLee
 */
public class FilterServletOutputStream extends ServletOutputStream{

	private DataOutputStream stream;

	public FilterServletOutputStream(OutputStream output){
		stream = new DataOutputStream(output);
	}

	public void write(int b) throws IOException {
		stream.write(b);
	}

	public void write(byte[] b) throws IOException {
		stream.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		stream.write(b, off, len);
	}

}